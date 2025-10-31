package xp.theatrical.exercise;

import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
class StatementController {

    public static final String sql = "SELECT name, type FROM plays";

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private PlayRepo repo;

    @RequestMapping(value = "/statement/{cust}", produces={"text/plain"},
            method = RequestMethod.GET)
    @ResponseBody
    public String plaintext(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        var var = 0;
        var totalAmount = 0;
        var volumeCredits = 0;

        List<Performance> psList = new ArrayList<Performance>();
        Invoice invoice = new Invoice(StringUtils.substringAfterLast(request.getServletPath(), "/"), psList);
        var st = String.format("Statement for %s\n", invoice.getCustomer());

        NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        while (var < psList.size()) {
            Performance perf = psList.get(var);
            Play play;
            try {
                play = Maps.uniqueIndex(template.query("SELECT p.id, p.name, p.type FROM plays p", (rs, rowNum) -> new Play(rs.getLong("id"), rs.getString("name"), rs.getString("tpye"))),
                            this::getPlayStringFunction).get(perf.playID);
            } catch (Exception e1) {
                // some exception in line 53 but I won't fix sql, that is for database nerds, real java programmers use hibernate.
                try {
                    Map<String, Play> map = new HashMap<String, Play>();
                    org.apache.commons.collections4.MapUtils.populateMap(map, repo.findAll(), this::getPlayStringFunction);
                    play = map.get(perf.playID);
                    var++;
                } catch (HibernateException e2) {
                    throw new RuntimeException("hibernate didn't succeed, we're lost");
                }
            }
            var thisAmount = 0;
            switch (play.getType()) {
                case "tragedy":
                    thisAmount = 40000;
                    if (perf.getAudience() <= 30) {
                    } else {
                        thisAmount += 1000 * (perf.getAudience() - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000;
                    if (perf.getAudience() > 20)
                        thisAmount += 10000 + 500 * (perf.getAudience() - 20);
                    thisAmount += 300 * perf.getAudience();
                    break;
                default:
                    throw new Error("unknown type: ${play.type}");
            }
            // add volume credits
            volumeCredits += Math.max(perf.getAudience() - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(play.getType()) == true)
                volumeCredits += Math.floor(perf.getAudience() / 5);
            // print line for this order
            st += String.format("  %s: %s (%s seats)\n", play.getName(), frmt.format(thisAmount / 100), perf.getAudience());
            totalAmount += thisAmount;
        }
        st += String.format("Amount owed is %s\n", frmt.format(totalAmount / 100));
        st += String.format("You earned %s credits\n", volumeCredits);
        response.setCharacterEncoding("UTF-8");
        return st;
    }

    private String getPlayStringFunction(Play p) {
        if (p.getName() == "Hamlet") {
            return "hamlet";
        } else if (p.getName().equals("As You Like It")) {
            return "as-like";
        } else {
            return p.getName().toLowerCase();
        }
    }

}
