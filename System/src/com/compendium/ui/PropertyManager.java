/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.ExternalConnection;

/**
 * This class provides an interace for a property file. T parameter is
 * an interface for properties and each property has corresponded accessor
 * and mutator method (see <code>ApplicationProperties</code> as an example).
 *
 * @param <T> The interface for the loaded property file.
 */
public class PropertyManager<T> {
    private final String fileName;
    private final Class<T> clazz;
    private final Pattern prefixPattern;
    private T cachedProperties;

    public PropertyManager(Class<T> clazz, String fileName) {
        this.clazz = clazz;
        this.fileName = fileName;
        this.prefixPattern = Pattern.compile("(get|is|set)(.*)");
    }

    /**
     * Load the format properties for the appropriat file and return a proxy
     * object which implements T interface and provides access to
     * the underlying properties.
     *
     * @return The proxy object which provides access to the underlying properties.
     */
    @SuppressWarnings("unchecked")
    public T load() {
        if (cachedProperties == null) {
            Properties properties = new Properties();
            readProperiesFromFile(properties, createFileName(fileName));

            String resetFileName = createFileName("Reset_" + fileName);
            if (new File(resetFileName).exists()) {
                readProperiesFromFile(properties, resetFileName);
                new File(resetFileName).delete();
            }

            cachedProperties = (T) Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[] { clazz },
                    new PropertiesDelegator(properties));
        }
        return cachedProperties;
    }

    private static void readProperiesFromFile(Properties properties, String fileName) {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(fileName);
            properties.load(inStream);
        } catch (IOException e) {
            System.out.println("Loading of '" + fileName + "' property file failed. Default properties will be used.");
        } finally {
            if (inStream != null)
                try {
                    inStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    /**
     * Save all properties into the fileName file.
     */
    public void save() {
        if (cachedProperties == null) {
            return;
        }
        Properties newProperties = new Properties();
        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            Matcher matcher = prefixPattern.matcher(methodName);
            if (matcher.find()) {
                String prefix = matcher.group(1);
                String propertyName = firstToLowerCase(matcher.group(2));
                if (prefix.equals("get") || prefix.equals("is")) {
                    try {
                        newProperties.put(propertyName, method.invoke(cachedProperties).toString());
                    } catch (Throwable e) {
                        System.out.println("Failed to save '" + methodName + "' property. The root cause: " + e.getMessage());
                    }
                }
            }
        }
        try {
            newProperties.store(new FileOutputStream(createFileName(fileName)), "File: " + fileName + "\nAPI: " + clazz.getName());
        } catch (IOException e) {
            new RuntimeException("Saving of '" + fileName + "' property file failed.", e);
        }
    }

    private String createFileName(String sortFileName) {
        return ProjectCompendium.sHOMEPATH + ProjectCompendium.sFS + "System" + ProjectCompendium.sFS + "resources" + ProjectCompendium.sFS + sortFileName;
    }

    private String firstToLowerCase(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    private class PropertiesDelegator implements InvocationHandler {
        private final Properties properties;

        public PropertiesDelegator(Properties properties) {
            this.properties = properties;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            Object result = null;
            String methodName = method.getName();
            getDefaultValue(method);

            Matcher matcher = prefixPattern.matcher(methodName);
            if (matcher.find()) {
                String prefix = matcher.group(1);
                String propertyName = firstToLowerCase(matcher.group(2));
                if (prefix.equals("get") || prefix.equals("is")) {
                    result = getProperty(propertyName, method);
                } else if (prefix.equals("set")) {
                    if (args.length != 1) {
                        new RuntimeException("Setter method has to have one parameter.");
                    }
                    setProperty(propertyName, args[0]);
                }
            } else {
                new RuntimeException(
                        "Method '" + methodName + "' has invalid name. " +
                        		"It should has 'get', 'is' or 'set' prefix.");
            }
            return result;
        }

        private void setProperty(String propertyName, Object object) {
            properties.setProperty(propertyName, object != null ? object.toString() : "");
        }

        @SuppressWarnings("unchecked")
        private Object getProperty(String propertyName, Method method) {
            String propertyValue = getStringValue(propertyName, method);
            Object result = propertyValue;
            Class<?> returnType = method.getReturnType();
            if (returnType == Integer.class || returnType == Integer.TYPE) {
                result = parseInt(propertyValue);
            } else if (returnType == Long.class || returnType == Long.TYPE) {
                result = parseLong(propertyValue);
            } else if (returnType == Boolean.class || returnType == Boolean.TYPE) {
                result = parseBoolean(propertyValue);
            } else if (returnType == Double.class || returnType == Double.TYPE) {
                result = parseDouble(propertyValue);
            } else if (returnType == List.class) {
                result = parseList(propertyValue);
                if(method.getGenericReturnType() instanceof ParameterizedType){
                    ParameterizedType type =
                        (ParameterizedType) method.getGenericReturnType();
                    Type[] typeArguments = type.getActualTypeArguments();
                    if (typeArguments.length == 1 && typeArguments[0] == ExternalConnection.class) {
                        result = parseExternalConnection((List<String>) result);
                    }
                }
            }
            return result;
        }

        private List<ExternalConnection> parseExternalConnection(List<String> list) {
            List<ExternalConnection> result = new ArrayList<ExternalConnection>();
            for (String string : list) {
                result.add(parseExternalConnection(string));
            }
            return result;
        }

        private ExternalConnection parseExternalConnection(String string) {
            ExternalConnection result = new ExternalConnection();
            result.setType(ICoreConstants.MYSQL_DATABASE);

            String[] conectionProperties = string.split(",");
            if (conectionProperties.length > 0) result.setProfile(conectionProperties[0].trim());
            if (conectionProperties.length > 1) result.setServer(conectionProperties[1].trim());
            if (conectionProperties.length > 2) result.setLogin(conectionProperties[2].trim());
            if (conectionProperties.length > 3) result.setPassword(conectionProperties[3].trim());

            return result;
        }

        private List<String> parseList(String propertyValue) {
            List<String> result = new ArrayList<String>();
            for (String listItem : propertyValue.split(";")) {
                if (!listItem.trim().equals("")) result.add(listItem.trim());
            }
            return result;
        }

        private String getStringValue(String propertyName, Method method) {
            String result = properties.getProperty(propertyName);
            if (result == null) {
                PropertyAlias annotation =
                    method.getAnnotation(PropertyAlias.class);
                if (annotation != null) {
                    result = properties.getProperty(annotation.name());
                }
                if (result == null) {
                    result = getDefaultValue(method);
                }
            }
            return result;
        }

        private String getDefaultValue(Method method) {
            String result = "";
            PropertyDefault annotation =
                method.getAnnotation(PropertyDefault.class);
            if (annotation != null) {
                result = annotation.value();
            }
            return result;
        }

        private double parseDouble(String propertyValue) {
            double result = 0;
            try {
                result = Double.parseDouble(propertyValue);
            } catch (NumberFormatException e) {
                System.out.println("Warning: parsing of '" + propertyValue +
                        "' to Double failed. Default '0' value is returned.");
            }
            return result;
        }

        private boolean parseBoolean(String propertyValue) {
            boolean result = true;
            try {
                result = Boolean.parseBoolean(propertyValue) ||
                    "yes".equals(propertyValue.toLowerCase().trim());
            } catch (NumberFormatException e) {
                System.out.println("Warning: parsing of '" + propertyValue +
                        "' to Boolean failed. Default 'true' value is returned.");
            }
            return result;
        }

        private int parseInt(String propertyValue) {
            int result = 0;
            try {
                result = Integer.parseInt(propertyValue);
            } catch (NumberFormatException e) {
                System.out.println("Warning: parsing of '" + propertyValue +
                        "' to Integer failed. Default '0' value is returned.");
            }
            return result;
        }

        private long parseLong(String propertyValue) {
            long result = 0;
            try {
                result = Integer.parseInt(propertyValue);
            } catch (NumberFormatException e) {
                System.out.println("Warning: parsing of '" + propertyValue +
                        "' to Long failed. Default '0' value is returned.");
            }
            return result;
        }
    }
}
