package com.sd.laborator.interfaces

interface IDiscoveryService {
    fun getFromRegistrar(name: String) : String?
    fun registerService(name: String, domain: String)
    fun unregister(name: String)
}