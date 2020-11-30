package org.zipli.socknet.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipli.socknet.model.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

//	@Autowired
//	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		User user = userRepository.findByEmail(username)
//				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		User user = new User(0,"Artemiy","dasdasd","");

		return new UserDetailsImpl(user);
	}

}
