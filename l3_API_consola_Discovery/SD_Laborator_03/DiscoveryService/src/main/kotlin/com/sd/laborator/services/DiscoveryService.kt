package com.sd.laborator.services

import com.sd.laborator.interfaces.IDiscoveryService
import org.springframework.stereotype.Service

@Service
class DiscoveryService : IDiscoveryService {

    private val serviceMap: MutableMap<String, String> = mutableMapOf()

    override fun getFromRegistrar(name: String): String? {
        if(serviceMap.containsKey(name))
            return serviceMap[name]
        return null
    }

    override fun registerService(name: String, domain: String) {
        serviceMap[name] = domain
    }

    override fun unregister(name: String) {
        serviceMap.remove(name)
    }

}