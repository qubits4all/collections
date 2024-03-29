package info.willdspann.collections.concurrent.queue;

import info.willdspann.collections.queue.PriorityQueueCorrectnessTester2.RequiredQueueTests;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 *
 * @author Will D. Spann
 * @version 1.0
 */
@RunWith(Categories.class)
@IncludeCategory(RequiredQueueTests.class)
@SuiteClasses({CHPQRequiredSTCorrectnessTester.class})
public class CHPQRequiredSTCorrectnessTestSuite { }
