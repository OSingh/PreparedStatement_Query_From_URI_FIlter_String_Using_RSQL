package rsql.models;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import static rsql.models.SQLOperatorEnum.*;

public class FilterCriteria {

    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public FilterCriteria next = null;
    private CustodiansFieldEnum columnName = null;
    private String operationName = null;
    final private List<String> values = new ArrayList<>();
    public String nextLogicalOperator = null;
    public boolean specificInserted = false;
    final private StringBuilder criteria = new StringBuilder();

    private FilterCriteria column(final String fieldName) {
        if(StringUtils.isEmpty(fieldName))
            return null;
        this.columnName = CustodiansFieldEnum.valueOf(fieldName);
        this.criteria.append(BRACE_BEG.getOperation() + CustodiansFieldEnum.valueOf(fieldName).getFieldWithTable());
        return this;
    }

    public CustodiansFieldEnum getColumn() {
        return this.columnName;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void setNext(final FilterCriteria nextCriteria) {
        if(nextCriteria != null) {
            FilterCriteria temp = this.next;
            this.next = nextCriteria;
            nextCriteria.next = temp;
        }
    }

    public FilterCriteria createCondition(final ComparisonNode comparisonNode) {
        FilterCriteria criteria = null;
        if(comparisonNode != null) {
            RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(comparisonNode.getOperator());
            if(operation != null) {
                final String field = comparisonNode.getSelector().toUpperCase();
                boolean isSupportedOperation = CustodiansFieldEnum.valueOf(field).getDataType().getOperators().contains(operation.getOperator());
                if(isSupportedOperation) {
                    this.values.addAll(comparisonNode.getArguments());
                    criteria = prepareFieldWithData(field, operation, comparisonNode.getArguments());
                } else {
                    throw new IllegalArgumentException("Invalid operator for the field - " + comparisonNode.getSelector());
                }
            } else {
                throw new IllegalArgumentException("Unsupported operation - " + comparisonNode.getOperator().getSymbol());
            }
        }
        return criteria;
    }

    private FilterCriteria prepareFieldWithData(final String field, final RsqlSearchOperation operation, final List<String> values) {
        final int noOfArguments = values.size();
        switch (operation) {
            case EQUAL:
                boolean isContainsSearch = values.stream().anyMatch(v -> v.contains("%"));
                if(isContainsSearch) {
                    this.column(field).like(noOfArguments);
                } else {
                    this.column(field).equalTo(noOfArguments);
                }
                break;
            case GREATER_THAN:
                this.column(field).greaterThan(noOfArguments);
                break;
            case LESS_THAN:
                this.column(field).lessThan(noOfArguments);
                break;
            case GREATER_THAN_OR_EQUAL:
                this.column(field).greaterThanEqualTo(noOfArguments);
                break;
            case LESS_THAN_OR_EQUAL:
                this.column(field).lessThanEqualTo(noOfArguments);
                break;
            case IN:
                this.column(field).in(noOfArguments);
                break;
            default:
                //Add default action
        }
        return this;
    }

    public FilterCriteria in(final int size) {
        if(size < 1 || this.criteria.length() == 0)
            return null;
        this.operationName = IN.name();
        this.criteria.append(IN.getOperation() + BRACE_BEG.getOperation());
        appendPlSql(size, Boolean.FALSE);
        this.criteria.append(BRACE_END.getOperation());
        return this;
    }

    public FilterCriteria equalTo(final int size) {
        if(size < 1 || this.criteria.length() == 0)
            return null;
        this.operationName = EQ.name();
        this.criteria.append(EQ.getOperation());
        appendPlSql(size, Boolean.FALSE);
        return this;
    }

    public FilterCriteria and(final FilterCriteria nextFilter) {
        this.nextLogicalOperator = AND;
        this.next = nextFilter;
        return nextFilter;
    }

    public FilterCriteria or(final FilterCriteria nextFilter) {
        this.nextLogicalOperator = OR;
        this.next = nextFilter;
        return nextFilter;
    }

    public FilterCriteria greaterThan(final int size) {
        if(size != 1 || this.criteria.length() == 0)
            return null;
        this.operationName = GT.name();
        this.criteria.append(GT.getOperation());
        appendPlSql(size, Boolean.FALSE);
        return this;
    }

    public FilterCriteria lessThan(final int size) {
        if(size != 1 || this.criteria.length() == 0)
            return null;
        this.operationName = LT.name();
        this.criteria.append(LT.getOperation());
        appendPlSql(size, Boolean.FALSE);
        return this;
    }

    public FilterCriteria greaterThanEqualTo(final int size) {
        if(size != 1 || this.criteria.length() == 0)
            return null;
        this.operationName = GE.name();
        this.criteria.append(GE.getOperation());
        appendPlSql(size, Boolean.FALSE);
        return this;
    }

    public FilterCriteria lessThanEqualTo(final int size) {
        if(size != 1 || this.criteria.length() == 0)
            return null;
        this.operationName = LE.name();
        this.criteria.append(LE.getOperation());
        appendPlSql(size, Boolean.FALSE);
        return this;
    }

    public FilterCriteria like(final int size) {
        if(size < 1 || this.criteria.length() == 0)
            return null;
        this.operationName = CONTAINS.name();
        this.criteria.append(CONTAINS.getOperation());
        appendPlSql(1, Boolean.FALSE);
        criteria.append(BRACE_END.getOperation());
        return this;
    }

    private void appendPlSql(final int size, final boolean range) {
        if(size == 1) {
            this.criteria.append(VALUE_OPERATOR.getOperation());
        } else {
            for(int i = 0; i < size; i++) {
                if(!range) {
                    this.criteria.append((i + 1 == size) ? VALUE_OPERATOR.getOperation() : (VALUE_OPERATOR.getOperation() + ", "));
                }
            }
        }
        this.criteria.append(this.operationName.equals(CONTAINS.name()) ? "" : BRACE_END.getOperation());
    }

    public StringBuilder getCriteria() {
        return this.criteria;
    }
}