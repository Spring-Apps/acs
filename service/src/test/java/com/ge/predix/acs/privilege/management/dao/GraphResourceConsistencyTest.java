package com.ge.predix.acs.privilege.management.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Collections;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ge.predix.acs.config.GraphConfig;
import com.ge.predix.acs.model.Attribute;
import com.ge.predix.acs.utils.JsonUtils;
import com.ge.predix.acs.zone.management.dao.ZoneEntity;
import com.thinkaurelius.titan.core.TitanFactory;

public class GraphResourceConsistencyTest {
    private static final JsonUtils JSON_UTILS = new JsonUtils();

    private GraphResourceRepository resourceRepository;
    private Graph graph;

    @BeforeClass
    public void setup() throws Exception {
        this.resourceRepository = new GraphResourceRepository();
        this.graph = TitanFactory.build().set("storage.backend", "inmemory").open();
        GraphConfig.createSchemaElements(this.graph);
        this.resourceRepository.setGraph(this.graph);
    }

    @Test(dataProvider = "resourcesForTestConcurrent", successPercentage = 97)
    public void testConcurrentWriteByZoneAndResourceIdentifier(final String resourceId) {
        System.out
                .println("thread_id: " + Long.toString(Thread.currentThread().getId()) + ", resource_id:" + resourceId);
        ZoneEntity z = new ZoneEntity();
        z.setName("z1");
        persistResourceAndAssertWithfindOne(z, resourceId, Collections.emptySet());
    }

    @DataProvider(parallel = true)
    public Object[][] resourcesForTestConcurrent() {
        return IntStream.range(0, 100).mapToObj(i -> new Object[] { "r" + Integer.toString(i) })
                .toArray(Object[][]::new);
    }

    private ResourceEntity persistResourceAndAssertWithfindOne(final ZoneEntity zoneEntity,
            final String resourceIdentifier, final Set<Attribute> attributes) {
        ResourceEntity resource = new ResourceEntity(zoneEntity, resourceIdentifier);
        resource.setAttributes(attributes);
        resource.setAttributesAsJson(JSON_UTILS.serialize(resource.getAttributes()));
        ResourceEntity resourceEntity = this.resourceRepository.save(resource);
        assertThat(this.resourceRepository.findOne(resource.getId()), equalTo(resource));
        return resourceEntity;
    }

    @Test(dataProvider = "resourcesForTestConcurrent", successPercentage = 95)
    public void testConcurrentWriteAndHierarchicalRead(final String resourceId) {
        ZoneEntity z = new ZoneEntity();
        z.setName("z1");
        persistResourceAndAssertWithGetInheritedAttributes(z, resourceId, Collections.emptySet());
    }

    private ResourceEntity persistResourceAndAssertWithGetInheritedAttributes(final ZoneEntity zoneEntity,
            final String resourceIdentifier, final Set<Attribute> attributes) {
        ResourceEntity resource = new ResourceEntity(zoneEntity, resourceIdentifier);
        resource.setAttributes(attributes);
        resource.setAttributesAsJson(JSON_UTILS.serialize(resource.getAttributes()));
        ResourceEntity resourceEntity = this.resourceRepository.save(resource);
        assertThat(this.resourceRepository.getEntityWithInheritedAttributes(zoneEntity, resourceIdentifier, attributes),
                equalTo(resource));
        return resourceEntity;
    }
}
