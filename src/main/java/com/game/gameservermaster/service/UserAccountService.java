package com.game.gameservermaster.service;

import com.game.gameservermaster.config.GMConstants;
import com.game.gameservermaster.db.UserAccountRepository;
import com.game.gameservermaster.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserAccountService implements UserDetailsService {

	@Autowired
	private UserAccountRepository accountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount userAccount = accountRepository.findById(username).orElseThrow(
			() -> new UsernameNotFoundException(String.format("username %s not found", username)));
		return new User(
			userAccount.getUsername(),
			userAccount.getPassword(),
			new HashSet<>(){{ add(new SimpleGrantedAuthority(GMConstants.CLIENT_ROLE)); }}
		);
	}

}