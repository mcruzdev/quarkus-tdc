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
    CardPageBuildItem devUI(List<ErrorBuildItem> errors) {

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();
        cardPageBuildItem.addBuildTimeData("errors", errors);

        cardPageBuildItem
                .addPage(Page.tableDataPageBuilder("REST errors").showColumn("message")
                        .showColumn("className")
                        .showColumn("methodName")
                        .buildTimeDataKey("errors")
                        .icon("font-awesome-solid:file-code").staticLabel(String.valueOf(errors.size())));
        return cardPageBuildItem;
    }

    @BuildStep
    void verifyBodyOnGet(ApplicationIndexBuildItem index, BuildProducer<ErrorBuildItem> errors) {

        Index jandex = index.getIndex();
        List<AnnotationInstance> annotations = jandex.getAnnotations(DotName.createSimple("jakarta.ws.rs.GET"));

        for (AnnotationInstance annotation : annotations) {
            MethodInfo methodInfo = annotation.target().asMethod();

            List<MethodParameterInfo> parameters = methodInfo.parameters();

            for (MethodParameterInfo param : parameters) {

                boolean isEmpty = param.annotations().isEmpty();
                if (isEmpty) {
                    errors.produce(
                            new ErrorBuildItem("Are you using REST? You should use @QueryParam, @PathParam or @Context",
                                    methodInfo.declaringClass().name().toString(),
                                    methodInfo.name()));
                }
            }
        }
    }
}
