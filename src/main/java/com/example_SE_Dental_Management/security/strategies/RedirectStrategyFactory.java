package com.example_SE_Dental_Management.security.strategies;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
public class RedirectStrategyFactory {

    private final Map<String, RedirectStrategy> strategyMap;

    // Spring will automatically inject all beans of type RedirectStrategy into this map.
    // The key will be the bean's name (e.g., "ROLE_ADMIN").
    public RedirectStrategyFactory(Map<String, RedirectStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public Optional<RedirectStrategy> getStrategy(String role) {
        return Optional.ofNullable(strategyMap.get(role));
    }
}
