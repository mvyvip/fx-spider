package com;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by lt on 2018/11/6 0006.
 */
public class ScriptTest2 {

    public static void main(String[] args) throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("javascript");
        try {
            String script = "function xxx() {\n"
                + "\t\t// setTimeout('location.href=location.pathname+location.search.replace(/[\\?|&]captcha-challenge/,\\'\\')', 1500);\n"
                + "\t\tvar cookie = '__jsl_clearance=1541487148.661|0|' + (function() {\n"
                + "\t\t\treturn ['BJkxD', ['' + [] + []][0].charAt((( - ~ {} << -~ {}) ^ ( + !''))) + (![] + [[]][0]).charAt(2) + ('' + [] + [[]][0]).charAt( - ~ {} + ( + !'') + 4) + [{} + []][0].charAt( - ~ {} + ( - ~ {} << ( - ~ !! [][[]] + [( - ~ {} << -~ {})] >> ( - ~ {} << -~ {})))), 'PX', [{} + []][0].charAt( - ~ {} + ( - ~ {} << ( - ~ !! [][[]] + [( - ~ {} << -~ {})] >> ( - ~ {} << -~ {})))) + ((( - ~ !! [][[]] + [2] >> 2)) / ~~ [] + [[]][0]).charAt(~~ {}) + (![] + [] + [[]][0]).charAt((( - ~ {} << -~ {}) ^ ( + !''))), 'E', [{} + [[]][0]][0].charAt(3), 'UG', [!( + []) + [] + []][0].charAt(( + [])), 'EvEMk', ((( - ~ !! [][[]] + [2] >> 2)) / ~~ [] + [] + [[]][0]).charAt( - ~ - ~ {} + 5) + [[{}][1] + [] + []][0].charAt(4) + [{} + [[]][0]][0].charAt([ - ~ []] + [ - ~ - ~ {}]), '%', ((( - ~ {} << -~ {}) ^ ( + !'')) + [] + []), 'D'].join('')\n"
                + "\t\t})() + ';Expires=Tue, 06-Nov-18 07:52:28 GMT;Path=/;'\n"
                + "\t\treturn cookie;\n"
                + "\t};";
            se.eval(script);
            Invocable inv2 = (Invocable) se;
            String res = (String) inv2.invokeFunction("xxx");
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
