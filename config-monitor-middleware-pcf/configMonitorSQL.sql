CREATE TABLE nbc_cust.url_store
  (
    app_name VARCHAR2(30) NOT NULL,
    app_url  VARCHAR2(100) NOT NULL,
    env VARCHAR2(100) 
);


insert into nbc_cust.url_store (app_name,app_url,env) values ('config-client','http://config-client.labash.inbcu.com','develop');