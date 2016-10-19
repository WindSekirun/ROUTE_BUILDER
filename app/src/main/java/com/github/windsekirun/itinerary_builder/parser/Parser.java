package com.github.windsekirun.itinerary_builder.parser;

import java.util.List;

//. by Haseem Saheed
public interface Parser {
    List<Route> parse() throws RouteException;
}