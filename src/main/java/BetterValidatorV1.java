import org.apache.commons.beanutils.BeanUtils;
import org.hamcrest.Matcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BetterValidatorV1 {
    //todo make matchers list of matchers
    protected Map<String,  Matcher> matchers;

    public BetterValidatorV1(){
        matchers = new HashMap<>();
    }

    public void getSetFields(BetterValidatorV1 obj){
        for (String key: matchers.keySet()){
            System.out.println(key);
           try {
                System.out.printf(
                        "%s -> %s",
                        key,
                        BeanUtils.getProperty(obj.getClass(), key)
                );
            } catch (Exception e){

           }
        }

    }

    public void getNonNullFields(){
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors()){
                if (!pd.getReadMethod().getName().equals("getClass")){
                    if (pd.getReadMethod().invoke(this) != null){
                        System.out.printf(
                                "%s -> %s%n",
                                pd.getReadMethod().getName(),
                                pd.getReadMethod().invoke(this));
                    }
                }
            }
        } catch (IntrospectionException e){
            System.out.println(e.toString());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
