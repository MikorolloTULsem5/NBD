package benchmarks;

import nbd.gV.courts.Court;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.repositories.CourtRepository;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CourtRepositoryBenchmark {
    @State(Scope.Benchmark)
    public static class BenchmarkSetup{
        CourtRepository repository = new CourtRepository();
        Court court1 = new Court(100, 200, 1);
        CourtMapper courtMapper1 = CourtMapper.toMongoCourt(court1);

        Court court2 = new Court(200, 200, 2);
        CourtMapper courtMapper2 = CourtMapper.toMongoCourt(court2);

        Court court3 = new Court(300, 300, 3);
        CourtMapper courtMapper3 = CourtMapper.toMongoCourt(court3);
        @Setup(Level.Invocation)
        public void setup(){
            repository.delete(court1.getCourtId());
            repository.delete(court2.getCourtId());
            repository.delete(court3.getCourtId());

            repository.create(courtMapper1);
            repository.create(courtMapper2);
            repository.create(courtMapper3);

            repository.getCache().delete(courtMapper1.getCourtId());
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1)
    @Warmup(iterations = 5, timeUnit = TimeUnit.SECONDS, time = 1)
    @Measurement(iterations = 5)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void cacheAccessTimeBenchmark(Blackhole blackhole, BenchmarkSetup setup){
        CourtMapper testCourtMapper = setup.repository.readByUUID(setup.court3.getCourtId());
        assertEquals(setup.courtMapper3,testCourtMapper);
        blackhole.consume(testCourtMapper);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1)
    @Warmup(iterations = 5, timeUnit = TimeUnit.SECONDS, time = 1)
    @Measurement(iterations = 5)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void databaseAccessTimeBenchmark(Blackhole blackhole, BenchmarkSetup setup){
        CourtMapper testCourtMapper = setup.repository.readByUUID(setup.court1.getCourtId());
        assertEquals(setup.courtMapper1,testCourtMapper);
        blackhole.consume(testCourtMapper);
    }
}
