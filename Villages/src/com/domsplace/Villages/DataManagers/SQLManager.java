/*
 * Copyright 2013 Dominic Masters and Jordan Atkins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.Villages.DataManagers;

import com.domsplace.Villages.Bases.Base;
import static com.domsplace.Villages.Bases.Base.getPlugin;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Enums.ManagerType;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;

public class SQLManager extends DataManager {
    private String host;
    private String port;
    private String username;
    private String password;
    private String database;
    private String prefix;
    
    private Connection connection;
    private Statement statement;
    
    public SQLManager() {
        super(ManagerType.SQL);
    }
    
    public void setupSQL(String host, String port, String username, String password, String database, String prefix) {
        this.host = host.replaceAll("%", "E");
        this.port = port.replaceAll("%", "E");
        this.username = username.replaceAll("%", "E");
        this.password = password.replaceAll("%", "E");
        this.database = database.replaceAll("%", "E");
        this.prefix = prefix.replaceAll("%", "E");
    }
    
    public String sqlEscape(Object o) {
        String str = o.toString();
        str = str.replace("\\", "\\\\");
        str = str.replace("'", "\\'");
        //str = str.replace("\0", "\\0");
        str = str.replace("\n", "\\n");
        str = str.replace("\r", "\\r");
        str = str.replace("\"", "\\\"");
        str = str.replace("\\x1a", "\\Z");
        str = str.replaceAll("%", "@");
        return str;
    }

    public String sqlUnescape(Object o) {
        String str = o.toString();
        str = str.replaceAll("\\\\", "\\");
        str = str.replaceAll("\\'", "'");
        str = str.replace("\\n", "\n");
        str = str.replace("\\r", "\r");
        str = str.replace("\\\"", "\"");
        str = str.replace("\\Z", "\\x1a");
        str = str.replaceAll("@", "%");
        return str;
    }
    
    public String prepareQuery(String query) {
        query = query.replaceAll("%db%", this.database);
        query = query.replaceAll("%t%", this.prefix);
        
        return query;
    }
    
    public boolean connect() {
        try {
            tryConnect();
            return true;
        } catch(ClassNotFoundException e) {
            error("You don't appear to have the MySQL Driver Installed.", e);
            return false;
        } catch(SQLException e) {
            error("Failed to Connect to the Database!", e);
            return false;
        }
    }
    
    public void tryConnect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://"+host+":"+port+"/";
        connection = DriverManager.getConnection(url,username,password);
    }
    
    public boolean query(String query) {
        try {
            return tryQuery(query);
        } catch(SQLException e) {
            error("Query Error!", e);
            return false;
        }
    }
    
    public boolean tryQuery(String query) throws SQLException {
        query = prepareQuery(query);
        Base.debug("[SQL] Running: \"" + query + "\"");
        PreparedStatement sqlStmt = connection.prepareStatement(query);
        boolean result = sqlStmt.execute(query);
        return true;
    }
    
    public long queryReturnID(String query) {
        try {
            return tryQueryReturnID(query);
        } catch(SQLException e) {
            error("Query Error!", e);
            return -1;
        }
    }
    
    public long tryQueryReturnID(String query) throws SQLException {
        query = prepareQuery(query);
        Base.debug("[SQL] Running (ID Return): \"" + query + "\"");
        Statement stmt = connection.createStatement();
        stmt.execute(query, Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        return rs.getLong(1);
    }
    
    public List<Map<String, String>> fetch(String query) {
        try {
            return tryFetch(query);
        } catch(Exception e) {
            return new ArrayList<Map<String, String>>();
        }
    }
    
    public List<Map<String, String>> tryFetch(String query) throws SQLException {
        query = this.prepareQuery(query);
        Base.debug("[SQL] Fetching: \"" + query + "\"");
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery(query);
        
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        
        while (result.next()){
            Map<String, String> data = new HashMap<String, String>();
            for(int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                data.put(result.getMetaData().getColumnName(i), result.getString(result.getMetaData().getColumnName(i)));
            }
            results.add(data);
        }
        
        return results;
    }
    
    @Override
    public void tryLoad() throws IOException {
        Base.useSQL = false;
        if(connection == null) return;
        
        //Create Tables
        InputStream queries = getPlugin().getResource("dbqueries.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(new InputStreamReader(queries));
        
        for(String s : yml.getKeys(false)) {
            String query = yml.getString(s);
            if(!query(query)) throw new IOException ("Failed to create " + s + " table.");
        }
        
        //Pre 2.09: Change Items Table ID from Int to String
        String alter = "ALTER TABLE `%db%`.`%t%Items` CHANGE `ID` `ID` VARCHAR(200) NOT NULL ;";
        try {this.query(alter);} catch(Throwable e) {}
        
        //Pre 2.14: Change Enchantments Table ID from Int to String and ID to Name
        boolean t = false;
        alter = "SELECT IF(COUNT(*)=1, '0', '1') as a FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '%db%' AND TABLE_NAME = '%t%ItemEnchantments' AND COLUMN_NAME = 'EnchantmentID' LIMIT 1;";
        t = this.fetch(alter).get(0).get("a").equalsIgnoreCase("0");
        
        if(t) {
            alter = "ALTER TABLE `%db%`.`%t%ItemEnchantments` CHANGE `EnchantmentID` `EnchantmentName` VARCHAR(96) NOT NULL;";
            try {this.query(alter);} catch(Throwable e) {}
        }
        
        Base.useSQL = true;
    }

    public String dateToSQL(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String now = ft.format(date);
        return now;
    }
    
    public Date sqlToDate(String sqlDate) {
        SimpleDateFormat fat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        try {
            return fat.parse(sqlDate);
        } catch (ParseException ex) {
            return new Date();
        }
    }
}
