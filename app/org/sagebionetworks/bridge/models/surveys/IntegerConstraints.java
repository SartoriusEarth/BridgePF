package org.sagebionetworks.bridge.models.surveys;

import java.util.EnumSet;

public class IntegerConstraints extends Constraints {

    private Long minValue;
    private Long maxValue;
    private Long step;
    
    public IntegerConstraints() {
        setDataType(DataType.INTEGER);
        setSupportedHints(EnumSet.of(UIHint.NUMBERFIELD, UIHint.SLIDER));
    }
    
    public Long getMinValue() {
        return minValue;
    }
    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }
    public Long getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }
    public Long getStep() {
        return step;
    }
    public void setStep(Long step) {
        this.step = step;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((maxValue == null) ? 0 : maxValue.hashCode());
        result = prime * result + ((minValue == null) ? 0 : minValue.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        IntegerConstraints other = (IntegerConstraints) obj;
        if (maxValue == null) {
            if (other.maxValue != null)
                return false;
        } else if (!maxValue.equals(other.maxValue))
            return false;
        if (minValue == null) {
            if (other.minValue != null)
                return false;
        } else if (!minValue.equals(other.minValue))
            return false;
        if (step == null) {
            if (other.step != null)
                return false;
        } else if (!step.equals(other.step))
            return false;
        return true;
    }
}
