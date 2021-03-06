package com.a.eye.skywalking.plugin.tomcat78x.define;

import com.a.eye.skywalking.api.plugin.interceptor.ConstructorInterceptPoint;
import com.a.eye.skywalking.api.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.a.eye.skywalking.api.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.a.eye.skywalking.plugin.tomcat78x.TomcatInterceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * {@link TomcatInstrumentation} presents that skywalking using class {@link TomcatInterceptor} to
 * intercept {@link org.apache.catalina.core.StandardEngineValve#invoke(Request, Response)}.
 *
 * @author zhangxin
 */
public class TomcatInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    /**
     * Enhance class.
     */
    private static final String ENHANCE_CLASS = "org.apache.catalina.core.StandardEngineValve";

    /**
     * Intercept class.
     */
    private static final String INTERCEPT_CLASS = "com.a.eye.skywalking.plugin.tomcat78x.TomcatInterceptor";

    @Override
    protected String enhanceClassName() {
        return ENHANCE_CLASS;
    }

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{new InstanceMethodsInterceptPoint() {
            @Override
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("invoke");
            }

            @Override
            public String getMethodsInterceptor() {
                return INTERCEPT_CLASS;
            }
        }};
    }
}
