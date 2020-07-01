package rsql.Utils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.collections4.CollectionUtils;
import rsql.models.FilterCriteria;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GenericRsqlSpecBuilder {

    public FilterCriteria createSpecification(Node node) {
        if (node instanceof LogicalNode) {
            return createSpecification((LogicalNode) node);
        }
        if (node instanceof ComparisonNode) {
            return createSpecification((ComparisonNode) node);
        }
        return null;
    }

    public FilterCriteria createSpecification(LogicalNode logicalNode) {
        List<FilterCriteria> specs = logicalNode.getChildren()
                .stream()
                .map(this::createSpecification)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(specs)) {
            return null;
        }
        FilterCriteria result = specs.get(0);
        while(result.next != null) {
            result = result.next;
        }
        if (logicalNode.getOperator() == LogicalOperator.AND) {
            for (int i = 1; i < specs.size(); i++) {
                result = result.and(specs.get(i));
            }
        } else if (logicalNode.getOperator() == LogicalOperator.OR) {
            for (int i = 1; i < specs.size(); i++) {
                result = result.or(specs.get(i));
            }
        }
        return specs.get(0);
    }

    public FilterCriteria createSpecification(ComparisonNode comparisonNode) {
        FilterCriteria condition = new FilterCriteria();
        condition.createCondition(comparisonNode);
        return condition;
    }
}