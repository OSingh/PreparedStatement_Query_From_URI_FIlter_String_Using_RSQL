package rsql.Utils;

import org.apache.commons.lang3.StringUtils;
import rsql.models.FilterCriteria;

import static rsql.models.SQLOperatorEnum.*;
import static rsql.models.FilterCriteria.AND;
import static rsql.models.FilterCriteria.OR;

public class SQLBuilder {

    public static final String SELECT_CLAUSE = "SELECT ";
    public static final String FROM_CLAUSE = " FROM ";
    public static final String JOIN_CLAUSE = " JOIN ";
    public static final String LEFT_JOIN_CLAUSE = " LEFT OUTER JOIN ";
    public static final String WHERE_CLAUSE = " WHERE ";
    public static final String UNION_ALL_CLAUSE = " UNION ALL ";
    public static final String DOT = ".";
    public static final String SPACE = " ";
    private String previousOperator = null;
    private StringBuilder query = new StringBuilder();

    public String getPreviousOperator() {
        return this.previousOperator;
    }

    public void setPreviousOperator(final String previousOperator) {
        if(previousOperator.equals(AND) || previousOperator.equals(OR)) {
            this.previousOperator = previousOperator;
        }
    }

    public SQLBuilder where(final FilterCriteria criteria) {
        if(criteria != null) {
            if(this.query.indexOf(WHERE_CLAUSE) == -1) {
                this.query.append(WHERE_CLAUSE);
            }
            this.query.append(this.previousOperator != null ? this.previousOperator : "").append(criteria.getCriteria());
            this.previousOperator = criteria.nextLogicalOperator;
        }
        return this;
    }

    public SQLBuilder orderBy(final String order) {
        if (StringUtils.isNotBlank(order) && StringUtils.isNotBlank(this.query)) {
            this.query.append(ORDERBY.getOperation()).append(order);
        }
        return this;
    }

    public SQLBuilder limit(final int limit) {
        if (limit > 0) {
            this.query.append(LIMIT.getOperation()).append(limit);
        }
        return this;
    }

    public SQLBuilder offset(final int offset) {
        if (offset >= 0) {
            this.query.append(OFFSET.getOperation()).append(offset);
        }
        return this;
    }

    public SQLBuilder groupBy(final String field) {
        if(!StringUtils.isEmpty(field)) {
            this.query.append(GROUPBY.getOperation()).append(field);
        }
        return this;
    }

    public SQLBuilder and(final String field) {
        if(!StringUtils.isEmpty(field)) {
            if(this.query.indexOf(WHERE_CLAUSE) == -1) {
                this.query.append(WHERE_CLAUSE);
            }
            this.query.append((this.previousOperator != null) ? this.previousOperator : "");
            this.query.append(field);
            this.previousOperator = AND;
        }
        return this;
    }

    public SQLBuilder and(final FilterCriteria criteria) {
        if(criteria != null && this.query.indexOf(WHERE_CLAUSE) != -1) {
            this.query.append(AND).append(criteria.getCriteria());
        }
        return this;
    }

    public SQLBuilder or(final String field) {
        if(!StringUtils.isEmpty(field)) {
            if(this.query.indexOf(WHERE_CLAUSE) == -1) {
                this.query.append(WHERE_CLAUSE);
            }
            this.query.append((this.previousOperator != null) ? this.previousOperator : "");
            this.query.append(field);
            this.previousOperator = OR;
        }
        return this;
    }

    public SQLBuilder or(final FilterCriteria criteria) {
        if(criteria != null && this.query.indexOf(WHERE_CLAUSE) != -1) {
            this.query.append(OR).append(criteria.getCriteria());
        }
        return this;
    }

    public SQLBuilder in(final String innerQuery) {
        if(!StringUtils.isEmpty(innerQuery) && !StringUtils.isEmpty(this.query)) {
            this.query.append(IN.getOperation())
                    .append(BRACE_BEG.getOperation())
                    .append(innerQuery)
                    .append(BRACE_END.getOperation());
        }
        return this;
    }

    public void clear() {
        this.query = new StringBuilder();
        this.previousOperator = null;
    }

    public String build() {
        return this.query.toString();
    }
}