import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

/** Handles redirect from backend after Google OAuth (token in query string). */
export default function OAuthCallback() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { overrideToken } = useAuth();

  useEffect(() => {
    const token = params.get('token');
    const err = params.get('error');
    if (err) {
      navigate(`/login?error=${encodeURIComponent(err)}`, { replace: true });
      return;
    }
    if (token) {
      overrideToken(token);
      navigate('/dashboard', { replace: true });
      return;
    }
    navigate('/login', { replace: true });
  }, [params, navigate, overrideToken]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50">
      <p className="text-slate-600 font-medium">Signing you in…</p>
    </div>
  );
}
