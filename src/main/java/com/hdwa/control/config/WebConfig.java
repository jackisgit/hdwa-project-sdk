package com.hdwa.control.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    /**
     * 修改自定义消息转换器
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 修改全局的全局日期格式 默认为yyyy-MM-dd HH:mm:ss
        JSON.DEFFAULT_DATE_FORMAT = "yyyyMMddHHmmss";
        //---处理字符串, 避免直接返回字符串的时候被添加了引号,处理中文乱码问题
        StringHttpMessageConverter smc = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(smc);
        converters.add(createFastJsonConverter());
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new BufferedImageHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter());
        converters.add(new Jaxb2RootElementHttpMessageConverter());
    }

    /**
     * @description: 放行swagger 静态资源文件，否则swagger页面打不开，报404错误
     * WebMvcConfigurationSupport导致自动配置失效， 因为WebMvc的自动配置都在WebMvcAutoConfiguration类中
     * 参考：https://blog.csdn.net/universsky2015/article/details/108064340
     * @param: registry
     * @return: void
     * @author: xingmaojun
     * @company: Persagy Technology Co.,Ltd
     * @since: 2020/10/27 20:16
     * @version: V1.0
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }


    /**
     * Description: 添加支持的类型
     *
     * @return List<MediaType>
     * @author luoguangyi
     * @since 2019年9月3日: 下午6:20:33 Update By luoguangyi 2019年9月3日: 下午6:20:33
     */
    private HttpMessageConverter createFastJsonConverter() {
        //===========替换框架json为fastjson
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);

        //创建配置类
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //---下划线转驼峰
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.CamelCase;
        fastJsonConfig.setSerializeConfig(serializeConfig);
        //---序列化格式
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteDateUseDateFormat,
                // List字段如果为null,输出为[],而非null
                // SerializerFeature.WriteNullListAsEmpty,
                // 是否显示为null的字段，加上会显示，取消就不会显示为空的字段
                // SerializerFeature.WriteMapNullValue,
                // 禁止循环引用
                SerializerFeature.DisableCircularReferenceDetect
                // SerializerFeature.WriteNullStringAsEmpty
        );
        fastJsonConfig.setDateFormat("yyyyMMddHHmmss");
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }

}