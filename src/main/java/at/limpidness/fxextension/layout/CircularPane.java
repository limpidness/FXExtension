package at.limpidness.fxextension.layout;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import static java.lang.Math.PI;

/**
 * CircularPane lay out it's children within a circular arrangement.
 */
public class CircularPane extends Pane {
    public enum ANGLE_MEASURE {
        RADIAN, DEGREE, GRADIAN, TURN;

        public double getRadian(Double value) {
            return switch (this) {
                case TURN -> value * PI * 2;
                case DEGREE -> value / 180.0 * PI;
                case RADIAN -> value;
                case GRADIAN -> value / 200.0 * PI;
            };
        }

        public double getRadian(Integer value) {
            return getRadian(value.doubleValue());
        }
    }

    private static final String ANGLE_CONSTRAINT = "circularpane-angle";
    private static final String RADIUS_ABS_CONSTRAINT = "circularpane-radius-abs";
    private static final String RADIUS_REL_CONSTRAINT = "circularpane-radius-rel";

    public static void setAngleMeasure(Node child, Number value, ANGLE_MEASURE measure) {
        double val = value.doubleValue();
        if (Double.isNaN(val) || val < 0) {
            throw new IllegalArgumentException("angle must be greater or equal to 0, but was " + value);
        }

        setConstraint(child, ANGLE_CONSTRAINT, measure.getRadian(val));
    }
    public static void setAngle(Node child, Number value) {
        setAngleMeasure(child, value, ANGLE_MEASURE.DEGREE);
    }
    public static Number getAngle(Node child) {
        return (Number)getConstraint(child, ANGLE_CONSTRAINT);
    }

    public static void setRadius(Node node, Number value) {
        double val = value.doubleValue();
        if (Double.isNaN(val) || val < 0)
            throw new IllegalArgumentException("radius must be greater or equal to 0, but was " + val);

        setConstraint(node, RADIUS_ABS_CONSTRAINT, val);
    }
    public static Number getRadius(Node node) {
        return (Number)getConstraint(node, RADIUS_ABS_CONSTRAINT);
    }
    public static void setRelativeRadius(Node node, Number value) {
        double val = value.doubleValue();
        if (Double.isNaN(val) || val < 0)
            throw new IllegalArgumentException("radius must be greater or equal to 0, but was " + val);

        setConstraint(node, RADIUS_REL_CONSTRAINT, val);
    }
    public static Number getRelativeRadius(Node node) {
        return (Number)getConstraint(node, RADIUS_REL_CONSTRAINT);
    }

    private static void setConstraint(Node node, Object key, Object value) {
        if (value == null)
            node.getProperties().remove(key);
        else
            node.getProperties().put(key, value);

        if (node.getParent() != null)
            node.getParent().requestLayout();
    }

    private static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            return node.getProperties().get(key);
        }
        return null;
    }

    @Override
    protected void layoutChildren() {
        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;

        for (Node child : getChildren()) {
            if (child.isManaged() && child.isResizable())
                child.autosize();

            Object radiusObject = getConstraint(child, RADIUS_ABS_CONSTRAINT);
            double radius = 0.0;
            if (radiusObject != null) {
                radius = (Double) radiusObject;
            }
            else {
                radiusObject = getConstraint(child, RADIUS_REL_CONSTRAINT);
                if (radiusObject != null)
                    radius = (Double) radiusObject * Math.min(centerX, centerY);
            }

            Object angleObject = getConstraint(child, ANGLE_CONSTRAINT);
            double angle = 0.0;
            if (angleObject != null) angle = (Double) angleObject;

            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            child.relocate(x - child.getBoundsInLocal().getWidth() / 2, y - child.getBoundsInLocal().getHeight() / 2);
        }
    }
}

