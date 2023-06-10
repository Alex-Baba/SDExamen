package com.sd.laborator.services

import com.sd.laborator.interfaces.CriteriaInterface
import com.sd.laborator.interfaces.FilterInformationInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service

@Service
class FilterInformationService : FilterInformationInterface {

    private var _criteria:CriteriaInterface? = null

    override fun filter(data: List<WeatherForecastData>): List<WeatherForecastData> {

        return _criteria?.meetCriteria(data) ?: mutableListOf()
    }

    override fun setCriteria(criteria: CriteriaInterface) {
        _criteria = criteria
    }
}