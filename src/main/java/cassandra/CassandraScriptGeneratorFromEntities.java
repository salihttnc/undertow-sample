package cassandra;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author b1r
 */
import com.datastax.driver.core.*;
import com.datastax.driver.mapping.MappedProperty;
import com.datastax.driver.mapping.MappingConfiguration;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.collect.ImmutableMap;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CassandraScriptGeneratorFromEntities {

    private static final Map<Class, DataType> BUILT_IN_CODECS_MAP = ImmutableMap.<Class, DataType>builder()
            .put(Long.class, DataType.bigint())
            .put(Boolean.class, DataType.cboolean())
            .put(Double.class, DataType.cdouble())
            .put(Float.class, DataType.cfloat())
            .put(Integer.class, DataType.cint())
            .put(Short.class, DataType.smallint())
            .put(Byte.class, DataType.tinyint())
            .put(long.class, DataType.bigint())
            .put(boolean.class, DataType.cboolean())
            .put(double.class, DataType.cdouble())
            .put(float.class, DataType.cfloat())
            .put(int.class, DataType.cint())
            .put(short.class, DataType.smallint())
            .put(byte.class, DataType.tinyint())
            .put(ByteBuffer.class, DataType.blob())
            .put(InetAddress.class, DataType.inet())
            .put(String.class, DataType.text())
            .put(Date.class, DataType.timestamp())
            .put(UUID.class, DataType.uuid())
            .put(LocalDate.class, DataType.date())
            .put(Duration.class, DataType.duration())
            .put(ZonedDateTime.class, TupleType.of(ProtocolVersion.NEWEST_SUPPORTED, CodecRegistry.DEFAULT_INSTANCE, DataType.timestamp(), DataType.text()))
            .build();
    private static final Predicate<List<?>> IS_NOT_EMPTY = ((Predicate<List<?>>) List::isEmpty).negate();

    public static StringBuilder convertEntityToSchema(final Class<?> entityClass, final String defaultKeyspace) {
        final Table table = Objects.requireNonNull(entityClass.getAnnotation(Table.class), () -> "The given entity " + entityClass + " is not annotated with @Table");
        final String keyspace = Optional.of(table.keyspace())
                .filter(((Predicate<String>) String::isEmpty).negate())
                .orElse(defaultKeyspace);
        final String ksName = table.caseSensitiveKeyspace() ? Metadata.quote(keyspace) : keyspace.toLowerCase(Locale.ROOT);
        final String tableName = table.caseSensitiveTable() ? Metadata.quote(table.name()) : table.name().toLowerCase(Locale.ROOT);

        final Set<? extends MappedProperty<?>> properties = MappingConfiguration.builder().build().getPropertyMapper().mapTable(entityClass);

        final List<? extends MappedProperty<?>> partitionKeys = Optional.of(
                properties.stream()
                        .filter(((Predicate<MappedProperty<?>>) MappedProperty::isComputed).negate())
                        .filter(MappedProperty::isPartitionKey)
                        .sorted(Comparator.comparingInt(MappedProperty::getPosition))
                        .collect(Collectors.toList())
        ).filter(IS_NOT_EMPTY).orElseThrow(() -> new IllegalArgumentException("No Partition Key define in the given entity"));

        final List<MappedProperty<?>> clusteringColumns = properties.stream()
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isComputed).negate())
                .filter(MappedProperty::isClusteringColumn)
                .sorted(Comparator.comparingInt(MappedProperty::getPosition))
                .collect(Collectors.toList());

        final List<MappedProperty<?>> otherColumns = properties.stream()
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isComputed).negate())
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isPartitionKey).negate())
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isClusteringColumn).negate())
                .sorted(Comparator.comparing(MappedProperty::getPropertyName))
                .collect(Collectors.toList());

        final StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");

        Optional.of(ksName).filter(((Predicate<String>) String::isEmpty).negate()).ifPresent(ks -> query.append(ks).append('.'));

        query.append(tableName).append("(\n").append(toSchema(partitionKeys));

        Optional.of(clusteringColumns).filter(IS_NOT_EMPTY).ifPresent(list -> query.append(",\n").append(toSchema(list)));
        Optional.of(otherColumns).filter(IS_NOT_EMPTY).ifPresent(list -> query.append(",\n").append(toSchema(list)));

        query.append(',').append("\nPRIMARY KEY(");
        query.append('(').append(join(partitionKeys)).append(')');

        Optional.of(clusteringColumns).filter(IS_NOT_EMPTY).ifPresent(list -> query.append(", ").append(join(list)));
        query.append("))");

        return query;
    }
    public static StringBuilder convertEntityToSchema(final Class<?> entityClass, final String defaultKeyspace, final long ttl) {
        final Table table = Objects.requireNonNull(entityClass.getAnnotation(Table.class), () -> "The given entity " + entityClass + " is not annotated with @Table");
        final String keyspace = Optional.of(table.keyspace())
                .filter(((Predicate<String>) String::isEmpty).negate())
                .orElse(defaultKeyspace);
        final String ksName = table.caseSensitiveKeyspace() ? Metadata.quote(keyspace) : keyspace.toLowerCase(Locale.ROOT);
        final String tableName = table.caseSensitiveTable() ? Metadata.quote(table.name()) : table.name().toLowerCase(Locale.ROOT);

        final Set<? extends MappedProperty<?>> properties = MappingConfiguration.builder().build().getPropertyMapper().mapTable(entityClass);

        final List<? extends MappedProperty<?>> partitionKeys = Optional.of(
                properties.stream()
                        .filter(((Predicate<MappedProperty<?>>) MappedProperty::isComputed).negate())
                        .filter(MappedProperty::isPartitionKey)
                        .sorted(Comparator.comparingInt(MappedProperty::getPosition))
                        .collect(Collectors.toList())
        ).filter(IS_NOT_EMPTY).orElseThrow(() -> new IllegalArgumentException("No Partition Key define in the given entity"));

        final List<MappedProperty<?>> clusteringColumns = properties.stream()
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isComputed).negate())
                .filter(MappedProperty::isClusteringColumn)
                .sorted(Comparator.comparingInt(MappedProperty::getPosition))
                .collect(Collectors.toList());

        final List<MappedProperty<?>> otherColumns = properties.stream()
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isComputed).negate())
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isPartitionKey).negate())
                .filter(((Predicate<MappedProperty<?>>) MappedProperty::isClusteringColumn).negate())
                .sorted(Comparator.comparing(MappedProperty::getPropertyName))
                .collect(Collectors.toList());

        final StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");

        Optional.of(ksName).filter(((Predicate<String>) String::isEmpty).negate()).ifPresent(ks -> query.append(ks).append('.'));

        query.append(tableName).append("(\n").append(toSchema(partitionKeys));

        Optional.of(clusteringColumns).filter(IS_NOT_EMPTY).ifPresent(list -> query.append(",\n").append(toSchema(list)));
        Optional.of(otherColumns).filter(IS_NOT_EMPTY).ifPresent(list -> query.append(",\n").append(toSchema(list)));

        query.append(',').append("\nPRIMARY KEY(");
        query.append('(').append(join(partitionKeys)).append(')');

        Optional.of(clusteringColumns).filter(IS_NOT_EMPTY).ifPresent(list -> query.append(", ").append(join(list)));
        query.append(')').append(") with default_time_to_live = ").append(ttl);

        return query;
    }

    private static String toSchema(final List<? extends MappedProperty<?>> list) {
        return list.stream()
                .map(property -> property.getMappedName() + ' ' + BUILT_IN_CODECS_MAP.getOrDefault(property.getPropertyType().getRawType(), DataType.text()))
                .collect(Collectors.joining(",\n"));
    }

    private static String join(final List<? extends MappedProperty<?>> list) {
        return list.stream().map(MappedProperty::getMappedName).collect(Collectors.joining(", "));
    }
}
