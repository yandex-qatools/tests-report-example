package ru.yandex.qatools.examples.report;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * User: eroshenkoam
 * Date: 7/16/13, 3:18 PM
 */
public class TemplateProcessorUtil {

    public static String processTemplate(Class clazz, String filePath, Object object) {
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(clazz, "/");
        InputStreamReader resourceStream = new InputStreamReader(clazz.getClassLoader().getResourceAsStream(filePath));
        return processTemplate(configuration, resourceStream, object);
    }

    public static String processTemplate(InputStream stream, Object object) {
        return processTemplate(new InputStreamReader(stream), object);
    }

    public static String processTemplate(Reader reader, Object object) {
        return processTemplate(new Configuration(), reader, object);
    }


    public static String processTemplate(Configuration configuration, Reader reader, Object object) {
        String name = new BigInteger(130, new SecureRandom()).toString(32);
        StringWriter writer = new StringWriter();
        try {
            Template template = new Template(name, reader, configuration);
            template.process(object, writer);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        return writer.toString();
    }
}