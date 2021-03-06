package cn.bluesking.framework.helper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.bluesking.framework.annotation.Action;
import cn.bluesking.framework.bean.Handler;
import cn.bluesking.framework.bean.Request;
import cn.bluesking.framework.util.ArrayUtil;
import cn.bluesking.framework.util.CollectionUtil;

/**
 * 控制器助手类
 * 
 * @author 随心
 *
 */
public final class ControllerHelper {

	/**
	 * 用于存放请求与处理器的映射关系
	 */
	private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();
	
	static {
		// 获取所有Controller类
		Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
		if(CollectionUtil.isNotEmpty(controllerClassSet)) {
			// 遍历控制器
			for(Class<?> controllerClass : controllerClassSet) {
				// 遍历控制器方法
				Method[] methods = controllerClass.getDeclaredMethods();
				if(ArrayUtil.isNotEmpty(methods)) {
					for(Method method : methods) {
						// 找到Action注释方法
						if(method.isAnnotationPresent(Action.class)) {
							Action action = method.getAnnotation(Action.class);
							// TODO Action注解标签配置读取
							String mapping = action.value()[0];
							if(mapping != null && mapping.matches("\\w+:/\\w*")) {
								String array[] = mapping.split(":");
								if(ArrayUtil.isNotEmpty(array) && array.length == 2) {
									// 截取请求方法和请求路径
									String requestMethod = array[0];
									String requestPath = array[1];
									// 创建请求对象
									Request request = 
											new Request(requestMethod, requestPath);
									// 创建请求对应处理器
									Handler handler = 
											new Handler(controllerClass, method);
									// 初始化到映射关系集合中
									ACTION_MAP.put(request, handler);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取Handler
	 * @param requestMehod [String]请求方法
	 * @param requestPath [String]请求路径
	 * @return
	 */
	public static Handler getHandler(String requestMethod, String requestPath) {
		Request request = new Request(requestMethod, requestPath);
		return ACTION_MAP.get(request);
	}
	
}
