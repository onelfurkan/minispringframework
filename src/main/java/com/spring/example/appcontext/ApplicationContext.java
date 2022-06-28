package com.spring.example.appcontext;

import custom.anotations.Autowired;
import com.spring.example.exception.MissingAnnotationException;
import custom.anotations.Component;
import custom.anotations.ComponentScan;
import custom.anotations.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContext
{
    private static final String PROJECT_SOURCE_PATH="C:\\Users\\B920\\Desktop\\AOP\\mini.spring.framework\\target\\classes\\";
    private static final HashMap<Class<?>, Object> context =  new HashMap<>();

    public ApplicationContext(Class<AppConfig> appConfigClass) throws Exception {
        Spring.initializerSpringContext(appConfigClass);
        Spring.injectAllBeans();
    }


    private static class Spring
    {
        private static void initializerSpringContext(Class<?> pClassObject) throws Exception {
            if(pClassObject.isAnnotationPresent(Configuration.class))
            {
                if(pClassObject.isAnnotationPresent(ComponentScan.class))
                {
                   ComponentScan comScanAnnotation =  pClassObject.getAnnotation(ComponentScan.class);
                   String valueOfPackageName = comScanAnnotation.value();

                   String packageStructure =  PROJECT_SOURCE_PATH+valueOfPackageName.replace(".","\\");
                   List<String> classFiles = findClassFiles(new File(packageStructure));


                   for(String classFile : classFiles)
                   {

                       String className = valueOfPackageName+"."+classFile.replace(".class","")
                               .replace("\\",".");
                       Class<?> classObject =  Class.forName(className);

                       if(classObject.isAnnotationPresent(Component.class))
                       {
                           Object classInstance =  classObject.newInstance();
                           context.put(classObject,classInstance);
                       }
                   }
                }
                else {
                    throw  new MissingAnnotationException("The claas "+pClassObject.getName()
                            +" has no @ComponentScan annotation.");
                }
            }
            else {
                throw  new MissingAnnotationException("The claas "+pClassObject.getName()
                        +" has no @Configuration annotation.");
            }
        }

        private static void injectAllBeans() throws IllegalAccessException
        {
            for (Map.Entry<Class<?>, Object> entry : context.entrySet())
            {
                ApplicationContext.getBean(entry.getValue().getClass());
            }
        }

        private static List<String> findClassFiles(File pFile) throws IOException
        {
            if(!pFile.exists())
            {
                throw new FileNotFoundException("Package "+ pFile+" does not exist !");
            }
            else
            {
                List<String> classFileList;

                try (Stream<Path> paths = Files.walk(Paths.get(pFile.getPath())))
                {
                     classFileList =paths
                            .filter(p -> Files.isRegularFile(p))   // not a directory
                            .map(p -> p.toString()) // convert path to string
                            .filter(f -> f.endsWith(".class"))
                            .collect(Collectors.toList());

                }
                catch (IOException e)
                {
                    throw  new IOException("Error : ");
                }

                return trimRedundantPrefix(classFileList);
            }
        }

        private static List<String> trimRedundantPrefix(List<String> pFilePahts)
        {
            List<String> finalClassPaths =  new ArrayList<>();

            for (String filePath : pFilePahts)
            {
                int lastOccurredIndex =  filePath.lastIndexOf("\\");
                int beforeLastOccurredIndex =  filePath.lastIndexOf("\\",lastOccurredIndex -1 );
                String classFileName = filePath.substring(beforeLastOccurredIndex+1,filePath.length());
                finalClassPaths.add(classFileName);
            }
            return finalClassPaths;
        }
    }

    public static  <T> T getBean(Class<T> tClass) throws IllegalAccessException {

        T object = (T) context.get(tClass);
        Field[] declaredFields = tClass.getDeclaredFields();

        injectBean(object,declaredFields);

        return object;
    }

    private static <T> void injectBean(T pObject, Field[] pDeclaredFields) throws IllegalAccessException {
        for(Field field :  pDeclaredFields)
        {
            if(field.isAnnotationPresent(Autowired.class))
            {
                field.setAccessible(true);
                Class<?> fieldType =  field.getType();
                Object innerObject = context.get(fieldType);
                field.set(pObject,innerObject);



                // We have to inject inner object of inner objects recursively
                // Imagine We have ProductService  It has been annotated with @Autowired
                // and ProductService have a field that is type of ProductRepository and
                // It also has been annotated with @Autowired annotation. This can be
                // a chain. ProductRepository also can have a field that  has been
                // annotated with @Autowired. So we should inject beans recursively to
                // inject all chain.
                Field[] declaredFieldsOfInnerObject = fieldType.getDeclaredFields();
                injectBean(innerObject,declaredFieldsOfInnerObject);
            }
        }
    }
}
