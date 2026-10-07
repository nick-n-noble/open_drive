import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import authHeader from "../services/DataService";
import AuthService from "../services/AuthService";

const Users = () => {
  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:8080/api/user", {
      method: 'get',
      headers: authHeader(),
    })
    .then((response) => {
      if (response.status == 401) {
        response.json()
        .then((data) => {
          console.log(data.message);
        })
        navigate('/login');
        return;
      }
      response.json()
      .then((data) => {
        console.log(data);
      })
      
    })
    .catch((error) => {
      console.error("ERROR:", error);
    })
  }, []);
  

  return(
    <div>
      <button onClick={() => {AuthService.logout()}}>Logout</button>
    </div>
  );
}

export default Users;