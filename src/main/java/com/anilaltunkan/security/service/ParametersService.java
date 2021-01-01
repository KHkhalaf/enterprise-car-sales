package com.anilaltunkan.security.service;

import com.anilaltunkan.security.model.Parameters;
import com.anilaltunkan.security.repository.ParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;


@Service
@CacheConfig(cacheNames = "Parameters")
public class ParametersService {
    @Autowired
    private ParametersRepository repo;

    @Cacheable(cacheNames = "NumberOfSeats")
    public int GetNumberOfSeats(){
        return repo.findAll().get(0).getNumberOfSeats();
    }

    @Cacheable(cacheNames = "ProfitRatio")
    public float GetProfitRatio(){
        return repo.findAll().get(0).getProfitRatio();
    }

    @Caching(evict = {@CacheEvict(value = "NumberOfSeats", allEntries = true),
                      @CacheEvict(value = "ProfitRatio", allEntries = true)})
    public void saveParameters(Parameters parameters){

        repo.save(parameters);
    }

}
