package com.poc.configServer.db.constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;

@RefreshScope
public class StringConstants {

    final static public String CRUD_POST_PROPERTIE_ENDPOINT = "http://127.0.0.1:8003/";
    final static public String EUREKA_INSTANCIAS_ENDPOINT = "http://127.0.0.1:8761/instancias";
}
