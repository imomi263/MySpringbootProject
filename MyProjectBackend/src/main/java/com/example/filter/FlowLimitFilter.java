package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.utils.Const.ORDER_LIMIT;

@Component
@Order(ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String address = request.getRemoteAddr();
        if(this.tryCount(address)){
            chain.doFilter(request, response);
        }else{
            writeBlockMessage(response);
        }

    }

    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden("work two much , try later").asJsonString());
    }
    private boolean tryCount(String ip){
        synchronized (ip.intern()) {
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.VERIFY_FLOW_BLOCK+ip))){
                return false;
            }
            return this.limitPeriodCheck(ip);
        }
    }


    private boolean limitPeriodCheck(String ip){
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.VERIFY_FLOW_LIMTT + ip))){
            long counter= Optional.ofNullable(stringRedisTemplate.opsForValue().increment(Const.VERIFY_FLOW_LIMTT+ip)).orElse(0L);
            if(counter>10){
                stringRedisTemplate.opsForValue().set(Const.VERIFY_FLOW_BLOCK+ip,"",30,TimeUnit.SECONDS);
                return false;
            }
        }else{
            stringRedisTemplate.opsForValue().set(Const.VERIFY_FLOW_LIMTT+ip, "1",3, TimeUnit.SECONDS);
        }
        return true;
    }

}
