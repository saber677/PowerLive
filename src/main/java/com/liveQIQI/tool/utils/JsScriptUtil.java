package com.liveQIQI.tool.utils;

import org.springframework.core.io.ClassPathResource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

public class JsScriptUtil {

    private static final String SCRIPT_PATH = "/js/script.js";

    public static void certifyRequest(Integer roomId, String url){
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
            ClassPathResource loader = new ClassPathResource(SCRIPT_PATH);
            InputStream fileInputStream = loader.getInputStream();
            Reader reader = new InputStreamReader(fileInputStream);
            engine.eval(reader);
            Invocable invocable = (Invocable) engine;
            invocable.invokeFunction("openConnection", roomId, url);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }




}
