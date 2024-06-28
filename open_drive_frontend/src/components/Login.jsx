import { useState } from "react";

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    if (!username || !password) {
      setError('Username and password are required');
      return;
    }
    const data = new FormData();
    data.append('username', username);
    data.append('password', password);
    setError('');

    fetch("http://localhost:8080/api/auth/login", {
      method: 'post',
      body: data,
    })
    .then((response) => {
      if (!response.ok) {
        response.json()
        .then((data) => {
          setError(data.message);
          console.log(data.message);
        });
        return;
      }
      response.json()
      .then((data) => {
        console.log(data.accessToken);
        localStorage.setItem("user", data.accessToken);
      })
    })
    .catch((error) => {
      console.log("Error:", error);
      setError("Something went wrong");
    })
  };

  return(
    <div>
      <h2>Login</h2>
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

        <button type="submit">Login</button>

      </form>
    </div>
  );
}

export default Login;