package xp.theatrical.exercise;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
/*
 * originally created by Lars, September 2019 ¯\_(ツ)_/¯
 */
public class Invoice {

    private String customer;
    private List<Performance> performances;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Invoice(String c, List<Performance> list) {
        if (c != null) {
            this.customer = c;
            /*
            https://community.oracle.com/blogs/pat/2004/10/23/stupid-scanner-tricks
            InputStream inputStream = Invoice.class.getClassLoader().getResourceAsStream("CheapExcel.txt");
            String fc = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            */

            var url = Resources.getResource("CheapExcel.txt");
            // cool, no input streams necessary, guava rulezzzz
            var fc = Resources.toString(url, StandardCharsets.UTF_8);

            var pers = fc.lines()
                    .dropWhile(l -> !l.startsWith(c))
                    .takeWhile(l -> l.startsWith(c))
                    .map(l -> l.split(", "))
                    .map(a -> Performance.builder()
                            .playID(a[1])
                            .audience(Integer.parseInt(a[2]))
                            .build())
                    .collect(Collectors.toList());
            if (BooleanUtils.isTrue( CollectionUtils.isEmpty(pers)) ){
                this.performances = UnmodifiableList.decorate(new ArrayList<Performance>());
            } else {
                // TODO: eclipse collections are the best, should rewrite everything to use them!
                this.performances = Lists.mutable.ofAll(pers);
            }
            list.addAll(pers);
        } else {
                this.customer = StringUtils.defaultIfEmpty(c, "");
                this.performances = ImmutableList.of();
                return;
        }
    }
}
