package rsql.Utils;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import rsql.models.FilterCriteria;

public class CustomRsqlVisitor implements RSQLVisitor<FilterCriteria, Void> {

    private GenericRsqlSpecBuilder builder;
    public CustomRsqlVisitor() {
        builder = new GenericRsqlSpecBuilder();
    }

    @Override
    public FilterCriteria visit(AndNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public FilterCriteria visit(OrNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public FilterCriteria visit(ComparisonNode node, Void param) {
        return builder.createSpecification(node);
    }
}