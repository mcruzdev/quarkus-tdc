package io.quarkiverse.tdc.deployment;

import java.util.List;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

class TdcProcessor {

    private static final String FEATURE = "tdc";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    CardPageBuildItem devUI(List<ProblemBuildItem> problems) {

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();
        cardPageBuildItem.addBuildTimeData("problems", problems);

        cardPageBuildItem
                .addPage(Page.tableDataPageBuilder("REST errors").showColumn("message")
                        .showColumn("className")
                        .showColumn("methodName")
                        .buildTimeDataKey("problems")
                        .icon("font-awesome-solid:file-code").staticLabel(String.valueOf(problems.size())));

        return cardPageBuildItem;

    }

    @BuildStep
    void rest(ApplicationIndexBuildItem index, BuildProducer<ProblemBuildItem> errorProducer) {

        Index jandex = index.getIndex();
        List<AnnotationInstance> annotations = jandex.getAnnotations(DotName.createSimple("jakarta.ws.rs.GET"));

        for (AnnotationInstance annotation : annotations) {
            AnnotationTarget target = annotation.target();

            MethodInfo asMethod = target.asMethod();

            List<MethodParameterInfo> parameters = asMethod.parameters();

            for (MethodParameterInfo param : parameters) {

                boolean isEmpty = param.annotations().isEmpty();

                if (isEmpty) {
                    errorProducer.produce(
                            new ProblemBuildItem("Você não deve adicionar corpo na requisição utilizando método GET",
                                    target.asMethod().declaringClass().simpleName(),
                                    target.asMethod().name()));
                }
            }
        }
    }
}
