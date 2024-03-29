package com.mzc.wiki.demo.mini.mvc.framework;

import com.mzc.wiki.demo.mini.mvc.framework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by miaozc on 2019-8-17.
 */
public class  MzcDispatcherServlet extends HttpServlet {
    //web.xml属性key
    private static final String LOCATION = "contextConfigLocation";
    //web.xml属性value存放,用于指定扫描的包
    private Properties p = new Properties();
    //存放扫描包底下的class
    private List<String> classNames = new ArrayList<String>();
    //IOC容器
    private Map<String,Object> ioc = new HashMap<String,Object>();
    //访问路径与执行器映射
    private List<Handler> handlerMapping = new ArrayList<Handler>();
    public MzcDispatcherServlet(){
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            doDispatch(req,resp); //开始始匹配到对应的方方法
        }catch(Exception e){
            //如果匹配过程出现异常，将异常信息打印出去
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
        }
    }
    /**
     * 匹配URL
     * @param req
     * @param resp
     * @return
     * @throws Exception
     */
    private void doDispatch(HttpServletRequest req,HttpServletResponse resp) throws Exception{
        try{
            Handler handler = getHandler(req);
            if(handler == null){
                //如果没有匹配上，返回404错误
                resp.getWriter().write("404 Not Found");
                return;
            }
            //获取方法的参数列表
            Class<?> [] paramTypes = handler.method.getParameterTypes();

            //保存所有需要自动赋值的参数值
            Object [] paramValues = new Object[paramTypes.length];
            Map<String,String[]> params = req.getParameterMap();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");

                //如果找到匹配的对象，则开始填充参数值
                if(!handler.paramIndexMapping.containsKey(param.getKey())){
                    continue;
                }
                int index = handler.paramIndexMapping.get(param.getKey());
                paramValues[index] = convert(paramTypes[index],value);
            }
            //设置方法中的request和response对象
            int reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
            int respIndex = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;

            handler.method.invoke(handler.controller, paramValues);

        }catch(Exception e){
            throw e;
        }
    }
    /**
     * url传过来的参数都是String类型的，HTTP是基于字符串协议
     * 只需要把String转换为任意类型就好
     * @param type
     * @param value
     * @return
     */
    private Object convert(Class<?> type,String value){
        if(Integer.class == type){
            return Integer.valueOf(value);
        }
        return value;
    }
    private Handler getHandler(HttpServletRequest req) throws Exception{
        if(handlerMapping.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (Handler handler : handlerMapping) {
            try{
                Matcher matcher = handler.pattern.matcher(url);
                //如果没有匹配上继续下一个匹配
                if(!matcher.matches()){ continue; }

                return handler;
            }catch(Exception e){
                throw e;
            }
        }
        return null;
    }
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件
        doLoadConfig(config.getInitParameter(LOCATION));
        //2.扫描指定类
        doScanner(p.getProperty("scanPackage"));
        //3.添加到ioc容器
        doInstance();
        //4.DI注入
        doAutowired();
        //5.初始化handlerMapping
        initHandlerMapping();
        System.out.println("MzcDispatcherServlet init end...");
    }
    private void doLoadConfig(String location){
        InputStream fis = null;
        try {
            fis = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));
            p.load(fis);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                if(null != fis){fis.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void initHandlerMapping(){
        if(ioc.isEmpty()){ return; }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(MzcController.class)){ continue; }

            String url = "";
            //获取Controller的url配置
            if(clazz.isAnnotationPresent(MzcRequestMapping.class)){
                MzcRequestMapping requestMapping = clazz.getAnnotation(MzcRequestMapping.class);
                url = requestMapping.value();
            }

            //获取Method的url配置
            Method [] methods = clazz.getMethods();
            for (Method method : methods) {

                //没有加RequestMapping注解的直接忽略
                if(!method.isAnnotationPresent(MzcRequestMapping.class)){ continue; }

                //映射URL
                MzcRequestMapping requestMapping = method.getAnnotation(MzcRequestMapping.class);
                String regex = ("/" + url + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new Handler(entry.getValue(),method,pattern));
                System.out.println("mapping " + regex + "," + method);
            }
        }

    }

    private void doInstance(){
        if(classNames.size() == 0){ return; }

        try{
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(MzcController.class)){
                    //默认将首字母小写作为beanName
                    String beanName = lowerFirst(clazz.getSimpleName());
                    ioc.put(beanName, clazz.newInstance());
                }else if(clazz.isAnnotationPresent(MzcService.class)){
                    MzcService service = clazz.getAnnotation(MzcService.class);
                    String beanName = service.value();
                    //如果用户设置了名字，就用用户自己设置
                    if(!"".equals(beanName.trim())){
                        ioc.put(beanName, clazz.newInstance());
                        continue;
                    }
                    //如果自己没设，就按接口类型创建一个实例
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces) {
                        ioc.put(i.getName(), clazz.newInstance());
                    }
                }else{
                    continue;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void doAutowired(){
        if(ioc.isEmpty()){ return; }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //拿到实例对象中的所有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if(!field.isAnnotationPresent(MzcAutowired.class)){
                    continue;
                }
                MzcAutowired autowired = field.getAnnotation(MzcAutowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true); //设置私有属性的访问权限
                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue ;
                }
            }
        }

    }


    private void doScanner(String packageName){
        //将所有的包路径转换为文件路径
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = null;
        try {
            String path = URLDecoder.decode(url.getPath(),"utf-8");
            dir = new File(path);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (dir!=null&&(dir.isFile()||dir.isDirectory())){
            for (File file : dir.listFiles()) {
                //如果是文件夹，继续递归
                if(file.isDirectory()){
                    doScanner(packageName + "." + file.getName());
                }else{
                    classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
                }
            }
        }
    }

    /**
     * 首字母小母
     * @param str
     * @return
     */
    private String lowerFirst(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private class Handler {
        //方法对应的实例
        protected Object controller;
        //方法
        protected Method method;
        //参数名,参数顺序
        protected Map<String,Integer> paramIndexMapping ;
        protected Pattern pattern;

        public Handler(Object controller, Method method, Pattern pattern) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            paramIndexMapping = new HashMap<String,Integer>();
            putParamIndexMapping(method);
        }

        private void putParamIndexMapping(Method method){
            //提取方法中加了注解的参数
            Annotation[] [] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length ; i ++) {
                for(Annotation a : pa[i]){
                    if(a instanceof MzcRequestParam){
                        String paramName = ((MzcRequestParam) a).value();
                        if(!"".equals(paramName.trim())){
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }
            //提取方法中的request和response参数
            Class<?> [] paramsTypes = method.getParameterTypes();
            for (int i = 0; i < paramsTypes.length ; i ++) {
                Class<?> type = paramsTypes[i];
                if(type == HttpServletRequest.class ||
                        type == HttpServletResponse.class){
                    paramIndexMapping.put(type.getName(),i);
                }
            }
        }
    }
}
