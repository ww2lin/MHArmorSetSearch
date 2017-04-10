package armorsetsearch.decorationsearch;

import java.util.ArrayList;
import java.util.List;

class SkillChartDataList {
    private List<SkillChartWithDecoration> skillChartWithDecorations = new ArrayList<>();

    public SkillChartDataList(List<SkillChartWithDecoration> skillChartWithDecorations) {
        this.skillChartWithDecorations = skillChartWithDecorations;
    }

    public SkillChartDataList() {
    }

    public void add(SkillChartWithDecoration skillChartWithDecoration) {
        skillChartWithDecorations.add(skillChartWithDecoration);
    }

    public void addAll(SkillChartDataList skillChartDataList) {
        skillChartWithDecorations.addAll(skillChartDataList.skillChartWithDecorations);
    }

    public SkillChartWithDecoration get(int index) {
        return skillChartWithDecorations.get(index);
    }

    public List<SkillChartWithDecoration> getSkillChartWithDecorations() {
        return skillChartWithDecorations;
    }

    public static SkillChartDataList cartesianProduct(SkillChartDataList list1, SkillChartDataList list2) {
        SkillChartDataList skillChartDataList = new SkillChartDataList();
        for (SkillChartWithDecoration skillChartWithDecoration1 : list1.skillChartWithDecorations) {
            for (SkillChartWithDecoration skillChartWithDecoration2 : list2.skillChartWithDecorations) {
                SkillChartWithDecoration newSkillChart = SkillChartWithDecoration.add(skillChartWithDecoration1, skillChartWithDecoration2);
                skillChartDataList.add(newSkillChart);
            }
        }
        return skillChartDataList;
    }
}
