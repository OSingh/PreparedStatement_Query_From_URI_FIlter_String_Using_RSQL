package rsql.models;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum FieldDataTypeEnum {
    DATE(RSQLOperators.EQUAL, RSQLOperators.LESS_THAN, RSQLOperators.GREATER_THAN, RSQLOperators.LESS_THAN_OR_EQUAL, RSQLOperators.GREATER_THAN_OR_EQUAL),
    TEXT(RSQLOperators.EQUAL, RSQLOperators.IN),
    ID(RSQLOperators.EQUAL),
    INTEGER(RSQLOperators.EQUAL, RSQLOperators.LESS_THAN, RSQLOperators.GREATER_THAN, RSQLOperators.LESS_THAN_OR_EQUAL, RSQLOperators.GREATER_THAN_OR_EQUAL),
    LONG(RSQLOperators.EQUAL, RSQLOperators.LESS_THAN, RSQLOperators.GREATER_THAN, RSQLOperators.LESS_THAN_OR_EQUAL, RSQLOperators.GREATER_THAN_OR_EQUAL),
    BOOLEAN(RSQLOperators.EQUAL);

    private List<ComparisonOperator> operators;

    private FieldDataTypeEnum(final ComparisonOperator... operators) {
        this.operators = Collections.unmodifiableList(Arrays.asList(operators));
    }

    public List<ComparisonOperator> getOperators() {
        return this.operators;
    }
}