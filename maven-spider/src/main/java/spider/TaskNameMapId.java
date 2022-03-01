package spider;

import java.util.HashMap;

public class TaskNameMapId {

    public static String sitTaskNameMap(String name){
        var map = new HashMap<String,String>();
        map.put("gateway-server","268");
        map.put("lnfp","260");
        map.put("nginx-fep","314");
        map.put("nginx","255");
        map.put("clm","263");
        map.put("actm","265");
        map.put("ccm","262");
        map.put("ccms","258");
        map.put("cip","251");
        map.put("gateway-erp","269");
        map.put("cgm","264");
        map.put("obdp","306");
        map.put("pcms","259");
        map.put("cps-web","249");
        map.put("sec","256");
        map.put("fdm","261");
        map.put("cps-server","250");
        map.put("workflow","252");
        map.put("rms","266");
        map.put("activiti","253");
        return map.get(name);
    }

    public static String uatTaskNameMap(String name){
        var map = new HashMap<String,String>();
        map.put("ccm","626");
        map.put("lnfp","64");
        map.put("ccms","59");
        map.put("cip","66");
        map.put("nginx-fep","477");
        map.put("gateway-service","76");
        map.put("nginx","476");
        map.put("clm","65");
        map.put("actm","67");
        map.put("gateway-erp","141");
        map.put("pcms","60");
        map.put("obdp","305");
        map.put("sec","69");
        map.put("cgm","68");
        map.put("uat","158");
        map.put("rms","78");
        map.put("uul-sit","159");
        map.put("fdm","75");
        map.put("obdp-spdown","351");
        map.put("workflow-app","74");
        map.put("workflow-activiti","72");
        map.put("agent-shell","349");
        map.put("cfm","71");
        map.put("limousine","205");
        map.put("gateway-credit","202");
        map.put("urule","73");
        map.put("dmpt","632");
        return map.get(name);
    }
}
