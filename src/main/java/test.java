import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.apache.commons.lang3.StringUtils;
import rsql.Utils.RSQLUtils;
import rsql.Utils.SQLBuilder;
import rsql.models.CustodiansFieldEnum;
import rsql.models.FieldDataTypeEnum;
import rsql.models.FilterCriteria;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static rsql.models.CustodiansFieldEnum.ID;
import static rsql.models.CustodiansFieldEnum.DOMAINID;
import static rsql.models.CustodiansFieldEnum.ALIASES;
import static rsql.models.CustodiansFieldEnum.ACTIVECUSTODIAN;
import static rsql.models.CustodiansFieldEnum.ACTIVECUSTODIANALIAS;
import static rsql.models.RSQLOperators.EQUAL;
import static rsql.models.FilterCriteria.AND;
import static rsql.models.FilterCriteria.OR;

public class test {

    private static final String CUSTODIAN_TOTAL_COUNT_SQL = "SELECT count(distinct custodian.id) FROM custodian JOIN custodian_alias ON custodian.id = custodian_alias.custodianid";
    private static final String CUSTODIAN_ALL_RESULTS_SQL = "SELECT custodian.id, custodian.name, custodian.description AS annotations, custodian.creationdate AS createdDate, custodian.lastmodifieddate AS lastmodifiedDate, string_agg(custodian_alias.alias, ', ') AS aliases FROM custodian JOIN custodian_alias ON custodian.id = custodian_alias.custodianid";
    private static final String CUSTODIAN_ALIAS_ALL_RESULTS_SQL = "select custodian_alias.custodianid from custodian_alias JOIN custodian on custodian_alias.custodianid = custodian.id";

    public static void main(String[] args) throws ParseException {
        String filter = "";
        String domainId = "";
        String orderByClause = "";
        int pageSize = 0;
        int offset = 9;
        FilterCriteria criteria = null;
        try {
            StringBuilder filterBuilder = new StringBuilder();
            if(!StringUtils.isEmpty(filter)) {
                filterBuilder.append(filter).append(";");
            }
            filterBuilder.append(StringUtils.join(DOMAINID, EQUAL.getSymbol(), domainId, ";", ACTIVECUSTODIAN, EQUAL.getSymbol(), "true;", ACTIVECUSTODIANALIAS,EQUAL.getSymbol(), "true"));
            criteria = RSQLUtils.getCriteriaList(filter);
        } catch (RSQLParserException | IllegalArgumentException e) {
            /*LOGGER.error("Failed to generate prepared statement query - {} {}", e.getMessage(), e);
            throwResponseErrorException(Response.Status.INTERNAL_SERVER_ERROR, CUSTODIAN_LIST_READ_ERROR);*/
        }
        final String countQuery = getCustodianListQuery(criteria, domainId, orderByClause, pageSize, offset, true);
        final String listQuery = getCustodianListQuery(criteria, domainId, orderByClause, pageSize, offset, false);

    }

    private static String getCustodianListQuery(final FilterCriteria criteria, final String domainId, final String orderByClause, final int pageSize, final int offset, final boolean isCountQuery) {
        final StringBuilder plSqlQuery = new StringBuilder();
        final SQLBuilder builder = new SQLBuilder();
        plSqlQuery.append(isCountQuery ? CUSTODIAN_TOTAL_COUNT_SQL : CUSTODIAN_ALL_RESULTS_SQL);
        if(criteria != null) {
            final SQLBuilder innerSQLBuilder = new SQLBuilder();

            FilterCriteria currentCriteria = criteria;
            while ((currentCriteria != null)) {
                if(currentCriteria.getColumn().equals(ALIASES)) {
                    //Add invalid criteria logger for Alias without domain for innerQuery
                    FilterCriteria domainNode = currentCriteria.next;
                    if((domainNode == null || !domainNode.getColumn().equals(DOMAINID)) && !domainNode.specificInserted) {
                        domainNode = new FilterCriteria();
                        //setting next criteria will help in setting values in PreparedStatement values
                        currentCriteria.setNext(domainNode);
                        domainNode.specificInserted = true;
                        domainNode.createCondition(new ComparisonNode(EQUAL, DOMAINID.name(), Collections.singletonList(domainId)));
                    }
                    innerSQLBuilder.where(currentCriteria).and(domainNode);
                    final String innerQuery = CUSTODIAN_ALIAS_ALL_RESULTS_SQL + innerSQLBuilder.build();
                    //to maintain correct logical relation between previous and current criteria we need previous logical operator of previous criteria
                    if(null == builder.getPreviousOperator() || builder.getPreviousOperator().equals(AND)) {
                        builder.and(ID.getFieldWithTable());
                    } else if(builder.getPreviousOperator().equals(OR)) {
                        builder.or(ID.getFieldWithTable());
                    }
                    builder.in(innerQuery);
                    //setting previous operator helps in maintaining correct logical operations between current and next criteria
                    builder.setPreviousOperator(innerSQLBuilder.getPreviousOperator());
                    innerSQLBuilder.clear();
                    //Skip next criteria as it's added in inner query
                    currentCriteria = currentCriteria.next;
                } else {
                    builder.where(currentCriteria);
                }
                currentCriteria = currentCriteria.next;
            }
        }
        if(!isCountQuery) {
            builder.groupBy(CustodiansFieldEnum.ID.getFieldWithTable()).orderBy(orderByClause).limit(pageSize).offset(offset).build();
        }
        plSqlQuery.append(builder.build());
        return plSqlQuery.toString();
    }

    private static void setValuesToStatement(final PreparedStatement statement, final FilterCriteria criteria, final AtomicInteger index) {
        try {
            if(criteria != null) {
                FilterCriteria currentCriteria = criteria;
                while (currentCriteria != null) {
                    final CustodiansFieldEnum fieldEnum = currentCriteria.getColumn();
                    currentCriteria.getValues().forEach(eachValue -> {
                        try {
                            setValues(statement, fieldEnum.getDataType(), eachValue, index);
                        } catch (SQLException | ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    });
                    currentCriteria = currentCriteria.next;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setValues(final PreparedStatement statement, final FieldDataTypeEnum dataType, final String value, final AtomicInteger index) throws SQLException, ParseException {
        switch (dataType) {
            case DATE:
                statement.setTimestamp(index.getAndIncrement(), new java.sql.Timestamp(RSQLUtils.getEpocTime(value)));
                break;
            case ID:
            case TEXT:
                statement.setString(index.getAndIncrement(), value);
                break;
            case BOOLEAN:
                statement.setBoolean(index.getAndIncrement(), Boolean.parseBoolean(value));
                break;
            case INTEGER:
                statement.setInt(index.getAndIncrement(), Integer.parseInt(value));
                break;
            default:
                //add default action
        }
    }
}