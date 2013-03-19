dataSource {
    pooled = false
    driverClassName = "org.hsqldb.jdbcDriver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
    // additional to standard
//    c3p0.initialPoolSize = 5
//    c3p0.acquire_increment = 5
//    c3p0.min_size = 5
//    c3p0.max_size = 20
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:hsqldb:file:devRiskAnalyticsDb;shutdown=true"
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:hsqldb:mem:devDB"
        }
    }
    mysql {
        dataSource {
            // Setting up mysql:
            //   create database p1rat;
            //   create user 'p1rat'@'localhost' identified by 'p1rat';
            //   grant all on table p1rat.* to 'p1rat'@'localhost';
            // required for batch uploads:
            //   grant file on *.* to 'p1rat'@'localhost';
            dbCreate = "update" // should always stay on update! use InitDatabase script to drop/create DB
            url = "jdbc:mysql://localhost/p1rat"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5Dialect"
            username = "p1rat"
            password = "p1rat"
            pooled = true
        }
    }
}