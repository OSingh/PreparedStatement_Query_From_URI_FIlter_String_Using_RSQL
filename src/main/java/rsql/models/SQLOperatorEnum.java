package rsql.models;

public enum SQLOperatorEnum {
    BRACE_BEG("("),
    BRACE_END(")"),
    VALUE_OPERATOR("?"),
    ON(" ON "),
    EQ(" = "),
    IN(" IN "),
    CONTAINS(" ILIKE "),
    LT(" < "),
    GT(" > "),
    LE(" <= "),
    GE(" >= "),
    RANGE(" BETWEEN "),
    LIMIT(" LIMIT "),
    OFFSET(" OFFSET "),
    ORDERBY(" ORDER BY "),
    GROUPBY(" GROUP BY ");

    private String operation;

    SQLOperatorEnum(final String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }
}
