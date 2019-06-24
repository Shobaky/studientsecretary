package shobaky.studientsecretary;

import java.util.List;

public interface asyncRespone {
    void processFinish(List<Material> materials);
    void processRemindFinish(List<reminder> reminders);
}
