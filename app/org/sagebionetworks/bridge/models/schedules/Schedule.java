package org.sagebionetworks.bridge.models.schedules;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.sagebionetworks.bridge.models.BridgeEntity;
import org.sagebionetworks.bridge.models.GuidCreatedOnVersionHolder;

import com.google.common.collect.Lists;

public class Schedule implements BridgeEntity {

    public static final String SCHEDULE_TYPE_NAME = "Schedule";
    public static final String TYPE_PROPERTY_NAME = "type";
    public static final String ACTIVITIES_PROPERTY = "activities";
    public static final String SCHEDULE_TYPE_PROPERTY = "scheduleType";
    public static final String EXPIRES_PROPERTY = "expires";
    public static final String ENDS_ON_PROPERTY = "endsOn";
    public static final String STARTS_ON_PROPERTY = "startsOn";
    public static final String CRON_TRIGGER_PROPERTY = "cronTrigger";
    public static final String LABEL_PROPERTY = "label";
    
    public static final String ACTIVITY_TYPE_PROPERTY = "activityType";
    public static final String ACTIVITY_REF_PROPERTY = "activityRef";
   
    private String label;
    private ScheduleType scheduleType;
    private String cronTrigger;
    private Long startsOn;
    private Long endsOn;
    private Long expires;
    private List<Activity> activities = Lists.newArrayList();
    
    public List<Activity> getActivities() {
        return activities;
    }
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
    public void addActivity(Activity activity) {
        checkNotNull(activity);
        this.activities.add(activity);
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public ActivityType getActivityType() {
        return (activities == null || activities.isEmpty()) ? null : activities.get(0).getActivityType();
    }
    public String getActivityRef() {
        return (activities == null || activities.isEmpty()) ? null : activities.get(0).getRef();
    }
    public ScheduleType getScheduleType() {
        return scheduleType;
    }
    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }
    public String getCronTrigger() {
        return cronTrigger;
    }
    public void setCronTrigger(String cronTrigger) {
        this.cronTrigger = cronTrigger;
    }
    public Long getStartsOn() {
        return startsOn;
    }
    public void setStartsOn(Long startsOn) {
        this.startsOn = startsOn;
    }
    public Long getEndsOn() {
        return endsOn;
    }
    public void setEndsOn(Long endsOn) {
        this.endsOn = endsOn;
    }
    public Long getExpires() {
        return expires;
    }
    public void setExpires(Long expires) {
        this.expires = expires;
    }
    public boolean isScheduleFor(GuidCreatedOnVersionHolder keys) {
        for (Activity activity : activities) {
            if (keys.keysEqual(activity.getSurvey())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activities == null) ? 0 : activities.hashCode());
        result = prime * result + ((cronTrigger == null) ? 0 : cronTrigger.hashCode());
        result = prime * result + ((endsOn == null) ? 0 : endsOn.hashCode());
        result = prime * result + ((expires == null) ? 0 : expires.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((scheduleType == null) ? 0 : scheduleType.hashCode());
        result = prime * result + ((startsOn == null) ? 0 : startsOn.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Schedule other = (Schedule) obj;
        if (activities == null) {
            if (other.activities != null)
                return false;
        } else if (!activities.equals(other.activities))
            return false;
        if (cronTrigger == null) {
            if (other.cronTrigger != null)
                return false;
        } else if (!cronTrigger.equals(other.cronTrigger))
            return false;
        if (endsOn == null) {
            if (other.endsOn != null)
                return false;
        } else if (!endsOn.equals(other.endsOn))
            return false;
        if (expires == null) {
            if (other.expires != null)
                return false;
        } else if (!expires.equals(other.expires))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (scheduleType != other.scheduleType)
            return false;
        if (startsOn == null) {
            if (other.startsOn != null)
                return false;
        } else if (!startsOn.equals(other.startsOn))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Schedule [label=" + label + ", scheduleType=" + scheduleType + ", cronTrigger=" + cronTrigger
                + ", startsOn=" + startsOn + ", endsOn=" + endsOn + ", expires=" + expires + ", activities="
                + activities + "]";
    }
}    
