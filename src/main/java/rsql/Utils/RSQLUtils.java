package rsql.Utils;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.lang3.StringUtils;
import rsql.models.FilterCriteria;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RSQLUtils {

    public static final String DEFAULTDATEFORMAT = "dd-MM-yy HH:mm:ss";

    public static FilterCriteria getCriteriaList(final String filter) {
        if(null == filter || StringUtils.isEmpty(filter)) {
            return null;
        }
        final Node filterCriteriaNode = new RSQLParser().parse(filter);
        return filterCriteriaNode.accept(new CustomRsqlVisitor());
    }

    public static Long getEpocTime(final String dateString) throws ParseException {
        SimpleDateFormat dateFormater = new SimpleDateFormat(DEFAULTDATEFORMAT);
        return dateFormater.parse(dateString).getTime();
    }
}
