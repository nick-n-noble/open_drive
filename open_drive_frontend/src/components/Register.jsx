import { useState } from "react";
import { Link } from "react-router-dom";

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSuccess('');
    if (!username || !password) {
      setError('Username and password are required');
      return;
    }
    const data = new FormData();
    data.append('username', username);
    data.append('password', password);
    setError('');
    
    
    fetch("http://localhost:8080/api/auth/register", {
      method: 'post',
      body: data,
    })
    .then(response => response)
    .then((response) => {
      if(!response.ok) {
        response.text()
        .then(data => {
          setError(data);
          console.log(data);
        })
        return;
      }
      response.text()
      .then(data => {
        setSuccess(data);
      })
      
    })
    .catch((error) => {
      console.error('Error:', error);
      setError('Something went wrong');
    });
  }

  return(
    <div>
      <h2>Register New User</h2>
      <form onSubmit={handleSubmit}>
        
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>

        <div>
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        {error && <p style={{ color: 'red' }}>{error}</p>}
        {success && <p style={{ color: 'green'}}>{success}</p>}

        <button type="submit">Register</button>

      </form>
      <Link to={'/login'}>Login</Link>
    </div>
  );
};

export default Register;