package rsql.models;

public enum CustodiansTableEnum {
    CUSTODIAN("custodian", "ct"),
    CUSTODIANALIAS("custodian_alias", "ca");

    private String tableName;
    private String alias;

    CustodiansTableEnum(final String tableName, final String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getAlias() {
        return this.alias;
    }
}