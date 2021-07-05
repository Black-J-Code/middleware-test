package com.lyq;

import com.aylaasia.corecloud.middleware.blockallowlist.annotation.BlockOrAllowList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * @author Jack
 */
@Slf4j
@Aspect
@Component
public class DoJoinPoint {

    @Autowired(required = false)
    private String allowListConfig;

    @Autowired(required = false)
    private String blockListConfig;

    @Pointcut("@annotation(com.aylaasia.corecloud.middleware.blockallowlist.annotation.BlockOrAllowList)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint pjp) throws RuntimeException, Throwable {
        Method method = getMethod(pjp);
        BlockOrAllowList blockAndAllowList = method.getAnnotation(BlockOrAllowList.class);
        String keyValue = getFiledValue(blockAndAllowList.key(), pjp);
        log.debug("middleware blockAndAllowList allowListConfig：{}，blockListConfig：{}，handler method：{}，value：{}",
                allowListConfig, blockListConfig, method.getName(), keyValue);
        if (ObjectUtils.isEmpty(keyValue)) {
            return pjp.proceed();
        }
        if (!ObjectUtils.isEmpty(allowListConfig)) {
            String[] split = allowListConfig.split(",");
            for (String str : split) {
                if (keyValue.equals(str)) {
                    return pjp.proceed();
                }
            }
            throw new RuntimeException("forbidden request because param is not in allowList");
        } else if (!ObjectUtils.isEmpty(blockListConfig)) {
            String[] split = blockListConfig.split(",");
            Boolean flag = false;
            for (String str : split) {
                if (keyValue.equals(str)) {
                    flag = true;
                }
            }
            if (!flag) {
                return pjp.proceed();
            } else {
                throw new RuntimeException("forbidden request because param is in blockList");
            }
        }
        return pjp.proceed();
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    private String getFiledValue(String filed, ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        String filedValue = null;
        String[] parameterNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (filed.equals(parameterNames[i])) {
                filedValue = args[i].toString();
                break;
            }
        }
        if (!ObjectUtils.isEmpty(filedValue)) {
            return filedValue;
        }
        for (Object arg : args) {
            try {
                if (ObjectUtils.isEmpty(filedValue)) {
                    filedValue = BeanUtils.getProperty(arg, filed);
                } else {
                    break;
                }
            } catch (Exception e) {
                if (args.length == 1) {
                    return args[0].toString();
                }
            }
        }
        return filedValue;
    }

//    private Object returnObject(DoWhiteList whiteList, Method method)
//    throws IllegalAccessException, InstantiationException {
//        Class<?> returnType = method.getReturnType();
//        String returnJson = whiteList.returnJson();
//        if ("".equals(returnJson)) {
//            return returnType.newInstance();
//        }
//        return JSON.parseObject(returnJson, returnType);
//    }

}
