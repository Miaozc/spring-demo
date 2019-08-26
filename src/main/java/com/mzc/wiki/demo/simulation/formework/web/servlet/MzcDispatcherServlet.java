package com.mzc.wiki.demo.simulation.formework.web.servlet;

import com.mzc.wiki.demo.simulation.formework.annotation.MzcController;
import com.mzc.wiki.demo.simulation.formework.annotation.MzcRequestMapping;
import com.mzc.wiki.demo.simulation.formework.context.MzcApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom on 2019/4/13.
 */
@Slf4j
public class MzcDispatcherServlet extends HttpServlet {
    public MzcDispatcherServlet(){
        super();
    }
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private MzcApplicationContext context;


    private List<MzcHandlerMapping> handlerMappings = new ArrayList<MzcHandlerMapping>();

    private Map<MzcHandlerMapping,MzcHandlerAdapter> handlerAdapters = new HashMap<MzcHandlerMapping,MzcHandlerAdapter>();

    private List<MzcViewResolver> viewResolvers = new ArrayList<MzcViewResolver>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            this.doDispatch(req,resp);
        }catch(Exception e){
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
//            new MzcModelAndView("500");

        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        MzcHandlerMapping handler = getHandler(req);

        if(handler == null){
            processDispatchResult(req,resp,new MzcModelAndView("404"));
            return;
        }

        //2、准备调用前的参数
        MzcHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        MzcModelAndView mv = ha.handle(req,resp,handler);

        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);

    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MzcModelAndView mv) throws Exception{
        //把给我的ModleAndView变成一个HTML、OuputStream、json、freemark、veolcity
        //ContextType
        if(null == mv){return;}

        //如果ModelAndView不为null，怎么办？
        if(this.viewResolvers.isEmpty()){return;}

        for (MzcViewResolver viewResolver : this.viewResolvers) {
            MzcView view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(),req,resp);
            return;
        }


    }

    private MzcHandlerAdapter getHandlerAdapter(MzcHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        MzcHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }


    private MzcHandlerMapping getHandler(HttpServletRequest req) throws Exception{
        if(this.handlerMappings.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (MzcHandlerMapping handler : this.handlerMappings) {
            try{
                Matcher matcher = handler.getPattern().matcher(url);
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
        //1、初始化ApplicationContext
        context = new MzcApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
        initStrategies(context);
    }


    //初始化策略
    protected void initStrategies(MzcApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);


        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);


        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(MzcApplicationContext context) {
    }

    private void initViewResolvers(MzcApplicationContext context) {

        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = null;
        try {
            String path = URLDecoder.decode(templateRootPath,"utf-8");
            templateRootDir = new File(path);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
             //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new MzcViewResolver(templateRoot));
        }

    }

    private void initRequestToViewNameTranslator(MzcApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(MzcApplicationContext context) {
    }

    private void initHandlerAdapters(MzcApplicationContext context) {

        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (MzcHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new MzcHandlerAdapter());
        }


    }

    private void initHandlerMappings(MzcApplicationContext context) {

        String [] beanNames = context.getBeanDefinitionNames();

        try {

            for (String beanName : beanNames) {

                Object controller = context.getBean(beanName);

                Class<?> clazz = controller.getClass();

                if(!clazz.isAnnotationPresent(MzcController.class)){
                    continue;
                }

                String baseUrl = "";
                //获取Controller的url配置
                if(clazz.isAnnotationPresent(MzcRequestMapping.class)){
                    MzcRequestMapping requestMapping = clazz.getAnnotation(MzcRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //获取Method的url配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {

                    //没有加RequestMapping注解的直接忽略
                    if(!method.isAnnotationPresent(MzcRequestMapping.class)){ continue; }

                    //映射URL
                    MzcRequestMapping requestMapping = method.getAnnotation(MzcRequestMapping.class);
                    //  /demo/query

                    //  (//demo//query)

                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handlerMappings.add(new MzcHandlerMapping(pattern,controller,method));
                    log.info("Mapped " + regex + "," + method);
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void initThemeResolver(MzcApplicationContext context) {
    }

    private void initLocaleResolver(MzcApplicationContext context) {
    }

    private void initMultipartResolver(MzcApplicationContext context) {
    }

}
