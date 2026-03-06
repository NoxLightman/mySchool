package com.example.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final LimitAction defaultAction = overclock -> overclock;

    /**
     * 单次频率限制
     * @param key
     * @param coldTime
     * @return
     */
    public boolean limitOnceCheck(String key, int coldTime){
        return this.internalCheck(key,1,coldTime, defaultAction);
    }

    /**
     * 多次访问限制
     * @param key 计数键
     * @param frequency 频率
     * @param period 周期
     * @return
     */
    public boolean limitPeriodCountCheck(String key, int frequency, int period){
        return this.internalCheck(key,frequency,period,defaultAction);
    }


    /**
     * 频率计时器
     * @param key 计数键
     * @param frequency 频率
     * @param period 周期
     * @param action 方法
     * @return
     */
    private boolean internalCheck(String key, int frequency, int period, LimitAction action){
        if(stringRedisTemplate.hasKey(key)){
            long value = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(key)).orElse(0L);
            stringRedisTemplate.expire(key, 3, TimeUnit.SECONDS);
            return action.run(value > frequency);
        } else {
            stringRedisTemplate.opsForValue().set(key, "1", frequency, TimeUnit.SECONDS);
            return true;
        }
    }

    public boolean limitPeriodCheck(String counterKey, String blockKey, int blockTime, int frequency, int period){
        return this.internalCheck(counterKey, frequency, period, (overclock) -> {
            if (overclock)
                stringRedisTemplate.opsForValue().set(blockKey, "", blockTime, TimeUnit.SECONDS);
            return !overclock;
        });
    }

    private interface LimitAction {
        boolean run(boolean overclock);
    }
}
