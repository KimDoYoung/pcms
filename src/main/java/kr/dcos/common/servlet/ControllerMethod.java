package kr.dcos.common.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CmsMvc가 해석할 수 있는 Controller임을 나타내는 아노테이션
 * alias는 콤마로 다른 이름을 여러개 줄 수 있게 한다 ex: go,gohome,goindex
 * @author Kim Do Young
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerMethod {
	String desc() default "";
	String alias() default "";  
}
