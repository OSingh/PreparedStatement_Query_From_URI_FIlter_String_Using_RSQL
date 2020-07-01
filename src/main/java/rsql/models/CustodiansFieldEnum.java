package rsql.models;

import org.apache.commons.lang3.StringUtils;

import static rsql.Utils.SQLBuilder.DOT;

public enum CustodiansFieldEnum {
    ID("id", FieldDataTypeEnum.ID, CustodiansTableEnum.CUSTODIAN),
    CUSTODIANID("id", FieldDataTypeEnum.ID, CustodiansTableEnum.CUSTODIAN),
    NAME("name", FieldDataTypeEnum.TEXT, CustodiansTableEnum.CUSTODIAN),
    ALIASES("alias", FieldDataTypeEnum.TEXT, CustodiansTableEnum.CUSTODIANALIAS),
    ACTIVECUSTODIAN("active", FieldDataTypeEnum.BOOLEAN, CustodiansTableEnum.CUSTODIAN),
    ACTIVECUSTODIANALIAS("active", FieldDataTypeEnum.BOOLEAN, CustodiansTableEnum.CUSTODIANALIAS),
    DOMAINID("domainid", FieldDataTypeEnum.ID, CustodiansTableEnum.CUSTODIAN),
    IQDOMAINID("domainid", FieldDataTypeEnum.ID, CustodiansTableEnum.CUSTODIAN),
    CREATEDDATE("creationdate", FieldDataTypeEnum.DATE, CustodiansTableEnum.CUSTODIAN),
    LASTMODIFIEDDATE("lastmodifieddate", FieldDataTypeEnum.DATE, CustodiansTableEnum.CUSTODIAN);

    private String field;
    private FieldDataTypeEnum dataType;
    private CustodiansTableEnum table;

    CustodiansFieldEnum(final String field, final FieldDataTypeEnum dataType, final CustodiansTableEnum table) {
        this.field = field;
        this.dataType = dataType;
        this.table = table;
    }

    public String getField() {
        return this.field;
    }

    public String getFieldWithTable() {
        return StringUtils.join(this.table.getTableName(), DOT, this.field);
    }

    public FieldDataTypeEnum getDataType() {
        return this.dataType;
    }

    public CustodiansTableEnum getTable() {
        return this.table;
    }
}