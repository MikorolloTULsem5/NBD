package benchmarks;

import nbd.gV.courts.Court;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.repositories.CourtRepository;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


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

        Court court4 = new Court(123, 312, 4);
        CourtMapper courtMapper4 = CourtMapper.toMongoCourt(court4);
        @Setup(Level.Trial)
        public void setup(){
            List<CourtMapper> list = repository.readAll();
            for(CourtMapper cm:list){
                repository.delete(UUID.fromString(cm.getCourtId()));
            }
            repository.create(courtMapper1);
            repository.create(courtMapper2);
            repository.create(courtMapper3);
            repository.create(courtMapper4);

            repository.delete(UUID.fromString(courtMapper1.getCourtId()));
            repository.delete(UUID.fromString(courtMapper2.getCourtId()));

            for(int i=5; i<=100;i++){
                Court temp = new Court(111,222,i);
                repository.create(CourtMapper.toMongoCourt(temp));
                if(i>=50){
                    repository.getCache().delete(temp.getCourtId().toString());
                }
            }
        }

        @TearDown(Level.Trial)
        public void tearDown(){
            List<CourtMapper> list = repository.readAll();
            for(CourtMapper cm:list){
                repository.delete(UUID.fromString(cm.getCourtId()));
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1)
    @Warmup(iterations = 2, timeUnit = TimeUnit.SECONDS, time = 1)
    @Measurement(iterations = 5)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void cacheAccessTimeBenchmark(Blackhole blackhole, BenchmarkSetup setup){
        CourtMapper testCourtMapper = setup.repository.readByUUID(setup.court3.getCourtId());
        blackhole.consume(testCourtMapper);
        testCourtMapper = setup.repository.readByUUID(setup.court4.getCourtId());
        blackhole.consume(testCourtMapper);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1)
    @Warmup(iterations = 2, timeUnit = TimeUnit.SECONDS, time = 1)
    @Measurement(iterations = 5)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void databaseAccessTimeBenchmark(Blackhole blackhole, BenchmarkSetup setup){
        CourtMapper testCourtMapper = setup.repository.readByUUID(setup.court1.getCourtId());
        blackhole.consume(testCourtMapper);
        testCourtMapper = setup.repository.readByUUID(setup.court2.getCourtId());
        blackhole.consume(testCourtMapper);
    }
}
